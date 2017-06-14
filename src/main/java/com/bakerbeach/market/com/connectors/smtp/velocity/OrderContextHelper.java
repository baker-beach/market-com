package com.bakerbeach.market.com.connectors.smtp.velocity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.com.connectors.smtp.velocity.TotalImpl;
import com.bakerbeach.market.com.connectors.smtp.velocity.TotalImpl.LineImpl;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.order.api.model.Order;
import com.bakerbeach.market.order.api.model.OrderItem;
import com.bakerbeach.market.order.api.model.OrderItem.OrderItemComponent;
import com.bakerbeach.market.order.api.model.OrderItem.OrderItemOption;;


public class OrderContextHelper {

	public static Map<String, Object> buildRenderContext(Order order) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("order", order);

		TotalImpl valueOfGoods = new TotalImpl();
		TotalImpl orderTotal = new TotalImpl();
		TotalImpl shippingTotal = new TotalImpl();

		List<Map<String, Object>> itemlist = new ArrayList<Map<String, Object>>();

		boolean group = false;

		if (order.getAdditionalInformations().containsKey("event_id")) {
			group = true;
		}
		result.put("group", group);
		result.put("delivery_time", order.getAdditionalInformations().get("delivery_date"));
		result.put("order_closing_time", order.getAdditionalInformations().get("order_date"));

		for (OrderItem item : order.getItems()) {
			if (item.getQualifier().equals("PRODUCT")) {
				HashMap<String, Object> maiItem = new HashMap<String, Object>();

				StringBuilder title = new StringBuilder();

				if (item.getTitle1() != null)
					title.append(item.getTitle1());
				if (item.getTitle2() != null)
					title.append(" ").append(item.getTitle2());
				if (item.getTitle3() != null)
					title.append(" ").append(item.getTitle3());

				for (OrderItemComponent component : item.getComponents().values()) {
					title.append(" mit");
					for (OrderItemOption option : component.getOptions().values()) {
						if (option.getTitle1() != null)
							title.append(" ").append(option.getTitle1());
						if (option.getTitle2() != null)
							title.append(" ").append(option.getTitle2());
						if (option.getTitle3() != null)
							title.append(" ").append(option.getTitle3());
					}
				}

				maiItem.put("title", title.toString());
				maiItem.put("quantity", item.getQuantity());
				maiItem.put("price", item.getTotalPrice());

				itemlist.add(maiItem);

				valueOfGoods.setGross(valueOfGoods.getGross().add(item.getTotalPrice()));
				orderTotal.setGross(orderTotal.getGross().add(item.getTotalPrice()));

				BigDecimal tax = item.getTotalPrice().divide((new BigDecimal(100)).add(item.getTaxPercent()), 2, BigDecimal.ROUND_HALF_UP).multiply(item.getTaxPercent());

				if (!valueOfGoods.getLines().containsKey(item.getTaxCode())) {
					valueOfGoods.getLines().put(item.getTaxCode(), new LineImpl(item.getTaxCode(), item.getTaxPercent()));
					orderTotal.getLines().put(item.getTaxCode(), new LineImpl(item.getTaxCode(), item.getTaxPercent()));
				}
				valueOfGoods.getLines().get(item.getTaxCode()).setTax(valueOfGoods.getLines().get(item.getTaxCode()).getTax().add(tax));
				orderTotal.getLines().get(item.getTaxCode()).setTax(orderTotal.getLines().get(item.getTaxCode()).getTax().add(tax));
			}
			if (item.getQualifier().equals("SHIPPING")) {
				if (!group) {
					orderTotal.setGross(orderTotal.getGross().add(item.getTotalPrice()));
					shippingTotal.setGross(shippingTotal.getGross().add(item.getTotalPrice()));

					BigDecimal tax = item.getTotalPrice().divide((new BigDecimal(100)).add(item.getTaxPercent()), 2, BigDecimal.ROUND_HALF_UP).multiply(item.getTaxPercent());

					if (!orderTotal.getLines().containsKey(item.getTaxCode()))
						orderTotal.getLines().put(item.getTaxCode(), new LineImpl(item.getTaxCode(), item.getTaxPercent()));
					shippingTotal.getLines().put(item.getTaxCode(), new LineImpl(item.getTaxCode(), item.getTaxPercent()));

					orderTotal.getLines().get(item.getTaxCode()).setTax(orderTotal.getLines().get(item.getTaxCode()).getTax().add(tax));
					shippingTotal.getLines().get(item.getTaxCode()).setTax(shippingTotal.getLines().get(item.getTaxCode()).getTax().add(tax));

				}
			}
		}

		if (group) {
			BigDecimal _sc = new BigDecimal((Double) order.getAdditionalInformations().get("shipping_cost"));

			result.put("shippingCosts", _sc);
			result.put("minParticipants", order.getAdditionalInformations().get("min_participants"));

			BigDecimal sc = _sc.divide(new BigDecimal((Integer) order.getAdditionalInformations().get("min_participants")), 2, BigDecimal.ROUND_HALF_UP);
			shippingTotal.setGross(sc);
			orderTotal.setGross(orderTotal.getGross().add(sc));

			if (!orderTotal.getLines().containsKey(TaxCode.NORMAL))
				orderTotal.getLines().put(TaxCode.NORMAL, new LineImpl(TaxCode.NORMAL, new BigDecimal(19.0)));
			shippingTotal.getLines().put(TaxCode.NORMAL, new LineImpl(TaxCode.NORMAL, new BigDecimal(19.0)));

			BigDecimal tax = sc.divide((new BigDecimal(100)).add(new BigDecimal(19)), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(19));

			orderTotal.getLines().get(TaxCode.NORMAL).setTax(orderTotal.getLines().get(TaxCode.NORMAL).getTax().add(tax));
			shippingTotal.getLines().get(TaxCode.NORMAL).setTax(shippingTotal.getLines().get(TaxCode.NORMAL).getTax().add(tax));
		}

		result.put("items", itemlist);
		result.put("valueOfGoods", valueOfGoods);
		result.put("orderTotal", orderTotal);
		result.put("shippingTotal", shippingTotal);

		return result;
	}

}
