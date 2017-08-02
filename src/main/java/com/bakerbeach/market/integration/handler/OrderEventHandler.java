package com.bakerbeach.market.integration.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.camel.Exchange;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.com.api.ComConnector;
import com.bakerbeach.market.com.api.ComConnectorException;
import com.bakerbeach.market.com.api.DataMapKeys;
import com.bakerbeach.market.com.api.MessageType;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.customer.api.service.CustomerService;
import com.bakerbeach.market.customer.api.service.CustomerServiceException;
import com.bakerbeach.market.order.api.model.Order;
import com.bakerbeach.market.order.api.model.OrderItem;
import com.bakerbeach.market.order.api.model.OrderItem.OrderItemComponent;
import com.bakerbeach.market.order.api.model.OrderItem.OrderItemOption;
import com.bakerbeach.market.order.api.service.OrderService;
import com.bakerbeach.market.order.api.service.OrderServiceException;

public class OrderEventHandler extends AbstractHandler {
	final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);

	private ComConnector comConnector;

	private OrderService orderService;

	private CustomerService customerService;

	public void order(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();
			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, (String) payload.get("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for order mail");
			}
			if (payload.containsKey("order_id")) {
				Order order = orderService.findOrderById((String) payload.get("shop_code"), (String) payload.get("order_id"));
				if (order != null) {
					data.put(DataMapKeys.ORDER, order);
					Customer customer = customerService.findById(order.getCustomerId());
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException(
							"order not found with id " + (String) payload.get("order_id") + " for order mail");
				}
			} else {
				throw new EventHandlerException("missing order_id parameter for order mail");
			}
			comConnector.generateMessageAndSend(MessageType.ORDER, data);
		} catch (EventHandlerException | ComConnectorException | CustomerServiceException | OrderServiceException e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	public void canceled(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();
			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, (String) payload.get("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for canceld mail");
			}
			if (payload.containsKey("order_id")) {
				Order order = orderService.findOrderById((String) payload.get("shop_code"), (String) payload.get("order_id"));
				if (order != null) {
					data.put(DataMapKeys.ORDER, order);
					Customer customer = customerService.findById(order.getCustomerId());
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException(
							"order not found with id " + (String) payload.get("order_id") + " for canceld mail");
				}
			} else {
				throw new EventHandlerException("missing order_id parameter for canceld mail");
			}
			comConnector.generateMessageAndSend(MessageType.CANCELD, data);
		} catch (EventHandlerException | ComConnectorException | CustomerServiceException | OrderServiceException e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	
	public void dispatched(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();
			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, (String) payload.get("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for order mail");
			}
			if (payload.containsKey("order_id")) {
				Order order = orderService.findOrderById((String) payload.get("shop_code"), (String) payload.get("order_id"));
				if (order != null) {
					data.put(DataMapKeys.ORDER, order);
					Customer customer = customerService.findById(order.getCustomerId());
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException(
							"order not found with id " + (String) payload.get("order_id") + " for order mail");
				}
			} else {
				throw new EventHandlerException("missing order_id parameter for order mail");
			}
			comConnector.generateMessageAndSend(MessageType.DISPATCHED, data);
		} catch (EventHandlerException | ComConnectorException | CustomerServiceException | OrderServiceException e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public void cancel(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();
			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, payload.containsKey("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for cancel mail");
			}
			if (payload.containsKey("order_id")) {
				Order order = orderService.findOrderById((String) payload.get("orderId"));
				if (order != null) {
					data.put(DataMapKeys.ORDER, order);
					Customer customer = customerService.findById(order.getCustomerId());
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException(
							"order not found with id " + (String) payload.get("order_id") + " for cancel mail");
				}
			} else {
				throw new EventHandlerException("missing order_id parameter for cancel mail");
			}
			comConnector.generateMessageAndSend(MessageType.CANCELD, data);
		} catch (EventHandlerException | ComConnectorException | CustomerServiceException | OrderServiceException e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public void warning(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();
			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, payload.containsKey("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for payment warning mail");
			}
			if (payload.containsKey("order_id")) {
				Order order = orderService.findOrderById((String) payload.get("orderId"));
				if (order != null) {
					data.put(DataMapKeys.ORDER, order);
					Customer customer = customerService.findById(order.getCustomerId());
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException("order not found with id " + (String) payload.get("order_id")
							+ " for payment warning mail");
				}
			} else {
				throw new EventHandlerException("missing order_id parameter for payment warning mail");
			}
			comConnector.generateMessageAndSend(MessageType.PAYMENT_WARNING, data);
		} catch (EventHandlerException | ComConnectorException | CustomerServiceException | OrderServiceException e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public void payment(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();
			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, payload.containsKey("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for payment mail");
			}
			if (payload.containsKey("order_id")) {
				Order order = orderService.findOrderById((String) payload.get("orderId"));
				if (order != null) {
					data.put(DataMapKeys.ORDER, order);
					Customer customer = customerService.findById(order.getCustomerId());
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException(
							"order not found with id " + (String) payload.get("order_id") + " for payment mail");
				}
			} else {
				throw new EventHandlerException("missing order_id parameter for payment mail");
			}
			comConnector.generateMessageAndSend(MessageType.PAYMENT, data);
		} catch (EventHandlerException | ComConnectorException | CustomerServiceException | OrderServiceException e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public void deliveryNote(Exchange ex) throws OrderServiceException, EventHandlerException, ComConnectorException {

		List<Order> orderList = new ArrayList<Order>();
		Set<String> emails = new TreeSet<String>();
		String orderIdsStr = "";
		Map<String, Map<String, Object>> lines = new HashMap<String, Map<String, Object>>();
		Map<String, Object> params = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());

		if (payload.containsKey("shop_code")) {
			params.put(DataMapKeys.SHOP_CODE, payload.containsKey("shop_code"));
		} else {
			throw new EventHandlerException("missing shop_code parameter for payment mail");
		}

		if (payload.containsKey("orders")) {
			@SuppressWarnings("unchecked")
			List<String> orderIdList = (List<String>) payload.get("orders");

			for (String orderId : orderIdList) {
				Order order = orderService.findOrderById(orderId);
				orderList.add(order);
				if (orderIdList.indexOf(orderId) == 0) {
					orderIdsStr = orderIdsStr + orderId;
					params.put("shippingAddress", order.getShippingAddress());
					params.put("billingAddress", order.getBillingAddress());
					params.put("delivery_time", order.getAdditionalInformations().get("delivery_date"));
					params.put("delivery_cost", order.getAdditionalInformations().get("shipping_cost"));
					params.put("order_closing_time", order.getAdditionalInformations().get("order_date"));
				} else {
					orderIdsStr = orderIdsStr + ", " + orderId;
				}
				emails.add(order.getCustomerEmail());
				for (OrderItem oi : order.getItems()) {
					if (oi.getQualifier().equals("PRODUCT")) {
						if (lines.containsKey(oi.getGtin())) {
							Map<String, Object> line = lines.get(oi.getGtin());
							line.put("qty", oi.getQuantity().add((BigDecimal) line.get("qty")));
						} else {
							Map<String, Object> line = new HashMap<String, Object>();
							line.put("gtin", oi.getGtin());
							line.put("title", oi.getTitle1());
							line.put("qty", oi.getQuantity());
							lines.put(oi.getGtin(), line);
						}
						for (OrderItemComponent oic : oi.getComponents().values()) {
							for (OrderItemOption oip : oic.getOptions().values()) {
								if (lines.containsKey(oip.getGtin())) {
									Map<String, Object> line = lines.get(oip.getGtin());
									line.put("qty", oip.getQuantity() + (Integer) line.get("qty"));
								} else {
									Map<String, Object> line = new HashMap<String, Object>();
									line.put("gtin", oip.getGtin());
									line.put("title", oip.getTitle1());
									line.put("qty", oip.getQuantity());
									lines.put(oip.getGtin(), line);
								}
							}
						}
					}
					else if(oi.getQualifier().equals("SHIPPING")){
						if(params.get("delivery_cost") == null)
							params.put("delivery_cost", oi.getTotalPrice());
					}
				}

			}
			params.put("orders", orderList);
			params.put("lines", lines.values());
			params.put("orderIds", orderIdsStr);
			
			if (payload.containsKey("emails")) {
				@SuppressWarnings("unchecked")
				List<String> _emails = (List<String>) payload.get("emails");
				for(String email: _emails){
					emails.add(email);
				}
			}
			if (payload.containsKey("payment")) {
				params.put("payment", payload.get("payment"));
			}

			for (String email : emails) {
				params.put(DataMapKeys.RECIPIENT, email);
				comConnector.generateMessageAndSend(MessageType.DISPATCHED, params);
			}

		}

	}

	public ComConnector getComConnector() {
		return comConnector;
	}

	public void setComConnector(ComConnector comConnector) {
		this.comConnector = comConnector;
	}

	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

}
