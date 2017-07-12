package com.bakerbeach.market.com.connectors.mandrill;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.bakerbeach.market.com.api.ComConnector;
import com.bakerbeach.market.com.api.ComConnectorException;
import com.bakerbeach.market.com.api.DataMapKeys;
import com.bakerbeach.market.com.api.MessageType;
import com.bakerbeach.market.com.connectors.mandrill.call.Call;
import com.bakerbeach.market.com.connectors.mandrill.call.SendTemplateCall;
import com.bakerbeach.market.com.connectors.mandrill.call.body.SendTemplateBody;
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.core.api.model.Total;
import com.bakerbeach.market.order.api.model.Order;
import com.bakerbeach.market.order.api.model.OrderItem;

public class MandrillConnector implements ComConnector {

	private Map<String, String> subjects = new HashMap<String, String>();

	public Map<String, String> getSubjects() {
		return subjects;
	}

	public void setSubjects(Map<String, String> subjects) {
		this.subjects = subjects;
	}

	private static String countryCodeStr = "GB:United Kingdom;US:United States;AL:Albania;AD:Andorra;AM:Armenia;AU:Australia;AT:Austria;AZ:Azerbaijan;BD:Bangladesh;BY:Belarus;BE:Belgium;BT:Bhutan;BA:Bosnia and Herzegovina;BN:Brunei;BG:Bulgaria;KH:Cambodia;CA:Canada;CN:China;CK:Cook Islands;HR:Croatia/Hrvatska;CY:Cyprus;CZ:Czech Republic;DK:Denmark;EE:Estonia;FO:Faroe Islands;FI:Finland;FR:France;GE:Georgia;DE:Germany;GI:Gibraltar;GR:Greece;GL:Greenland;VA:Holy See (City Vatican State);HK:Hong Kong;HU:Hungary;IS:Iceland;IN:India;ID:Indonesia;IE:Ireland;IM:Isle of Man;IT:Italy;JP:Japan;KI:Kiribati;LA:Laos;LV:Latvia;LI:Liechtenstein;LT:Lithuania;LU:Luxembourg;MO:Macau;MK:Macedonia;MJ:Malaysia;MT:Malta;MH:Marshall Islands;FM:Micronesia;MD:Moldova;MC:Monaco;MN:Mongolia;ME:Montenegro;MM:Myanmar;NR:Nauru;NP:Nepal;NL:Netherlands;NZ:New Zealand;NU:Niue;NO:Norway;PG:Papua New Guinea;PH:Philippines;PL:Poland;PT:Portugal;KR:Republic of Korea;CS:Republic of Serbia;RO:Romania;RU:Russia;SM:San Marino;SG:Singapore;SK:Slovakia;SI:Slovenia;SB:Solomon Islands;ES:Spain;LK:Sri Lanka;SJ:Svalbard and Jan Mayen Islands;SE:Sweden;CH:Switzerland;TW:Taiwan;TH:Thailand;TO:Tonga;TV:Tuvalu;UA:Ukraine;VU:Vanuatu;VN:Vietnam;WS:Western Samoa";
	private static final Map<String, String> countryCodes = new LinkedHashMap<String, String>();
	static {
		for (String kv : countryCodeStr.split(";")) {
			String k = StringUtils.substringBefore(kv, ":");
			String v = StringUtils.substringAfter(kv, ":");
			countryCodes.put(k, v);
		}
	}

	private String apiUrl;
	private String apiKey;
	private RestTemplate restTemplate;
	private TemplateResolver templateResolver = new TemplateResolver();

	private Map<String, String> bcc = new HashMap<String, String>();

	public MandrillConnector() {
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

	}

	public void call(Call call) {
		try {
			StringBuilder url = new StringBuilder(apiUrl);
			url.append(call.getPath()).append(".").append("json");
			System.out.println(url);
			ObjectMapper mapper = new ObjectMapper();
			call.getBody().setKey(apiKey);
			String body = mapper.writeValueAsString(call.getBody());
			System.out.println(body);
			String result = restTemplate.postForObject(url.toString(), body, String.class);
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void call(String url, String body) {
		System.out.println(url);
		System.out.println(body);
		String result = restTemplate.postForObject(url.toString(), body, String.class);
		System.out.println(result);
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String appKey) {
		this.apiKey = appKey;
	}

	@Override
	public void generateMessageAndSend(String messageType, Map<String, Object> data) throws ComConnectorException {
		Call call = null;

		if (MessageType.WELCOME.equals(messageType)) {
			call = buildWelcomeMailCall(data);
		} else if (MessageType.PASSWORD.equals(messageType)) {
			call = buildPasswordMailCall(data);
		} else if (MessageType.ORDER.equals(messageType)) {
			call = buildOrderMailCall(data);
		} else if (MessageType.DISPATCHED.equals(messageType)) {
			call = buildDispatchedMailCall(data);
		} else {
			throw new ComConnectorException();
		}

		if (call != null)
			call(call);

	}

	private Call buildWelcomeMailCall(Map<String, Object> data) throws ComConnectorException {
		SendTemplateCall stc = new SendTemplateCall();
		SendTemplateBody stb = stc.getSendTemplateBody();

		stb.setTemplateName(templateResolver.getTemplate(MessageType.WELCOME, (String) data.get(DataMapKeys.SHOP_CODE)));
		stb.setSubject(subjects.get(MessageType.WELCOME + "-" + (String) data.get(DataMapKeys.SHOP_CODE)));
		Customer customer = (Customer) data.get("customer");

		{
			HashMap<String, Object> item = new HashMap<String, Object>();
			item = new HashMap<String, Object>();
			item.put("name", "customer_email");
			item.put("content", customer.getEmail());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "first_name");
			item.put("content", customer.getFirstName());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "last_name");
			item.put("content", customer.getLastName());
			stb.getGlobalVars().add(item);
		}

		stb.addRecipient(customer.getEmail(), null, "to");
		String bccWelcome = bcc.get(MessageType.WELCOME);
		if (bccWelcome != null && !bccWelcome.isEmpty()) {
			for (String bccMail : bccWelcome.split(",")) {
				stb.addRecipient(bccMail, null, "bcc");
			}
		}

		return stc;
	}

	private Call buildPasswordMailCall(Map<String, Object> data) throws ComConnectorException {
		SendTemplateCall stc = new SendTemplateCall();
		SendTemplateBody stb = stc.getSendTemplateBody();

		stb.setTemplateName(templateResolver.getTemplate(MessageType.PASSWORD, (String) data.get(DataMapKeys.SHOP_CODE)));
		stb.setSubject(subjects.get(MessageType.PASSWORD + "-" + (String) data.get(DataMapKeys.SHOP_CODE)));
		Customer customer = (Customer) data.get("customer");

		{
			HashMap<String, Object> item = new HashMap<String, Object>();
			item = new HashMap<String, Object>();
			item.put("name", "customer_email");
			item.put("content", customer.getEmail());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "first_name");
			item.put("content", customer.getFirstName());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "last_name");
			item.put("content", customer.getLastName());
			stb.getGlobalVars().add(item);
		}

		{
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("name", "password");
			item.put("content", (String) data.get("password"));
			stb.getGlobalVars().add(item);
		}

		stb.addRecipient(customer.getEmail(), null, "to");
		String bccPassword = bcc.get(MessageType.PASSWORD);
		if (bccPassword != null && !bccPassword.isEmpty()) {
			for (String bccMail : bccPassword.split(",")) {
				stb.addRecipient(bccMail, null, "bcc");
			}
		}

		return stc;
	}

	@SuppressWarnings("deprecation")
	private Call buildOrderMailCall(Map<String, Object> data) throws ComConnectorException {
		SendTemplateCall stc = new SendTemplateCall();
		SendTemplateBody stb = stc.getSendTemplateBody();

		Order order = (Order) data.get("order");
		stb.setTemplateName(templateResolver.getTemplate(MessageType.ORDER, (String) data.get(DataMapKeys.SHOP_CODE)));

		// TODO: get locale from order ----
		Locale locale = Locale.GERMANY;
		if ("PUMPKIN_COM".equals(order.getShopCode())) {
			locale = Locale.UK;
		}

		// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMMM
		// yyyy");

		String subject = subjects.get(MessageType.ORDER + "-" + (String) data.get(DataMapKeys.SHOP_CODE));

		// MessageFormat.format(subject, order.getId(),
		// simpleDateFormat.format(order.getCreatedAt()));

		stb.setSubject(MessageFormat.format(subject, order.getId(), DateFormat.getDateInstance(DateFormat.SHORT, locale).format(order.getCreatedAt())));
		Customer customer = (Customer) data.get("customer");

		{
			HashMap<String, Object> item = new HashMap<String, Object>();
			item = new HashMap<String, Object>();
			item.put("name", "customer_id");
			item.put("content", customer.getId());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "customer_email");
			item.put("content", customer.getEmail());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "first_name");
			item.put("content", customer.getFirstName());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "last_name");
			item.put("content", customer.getLastName());
			stb.getGlobalVars().add(item);
		}

		{
			HashMap<String, Object> item = new HashMap<String, Object>();
			item = new HashMap<String, Object>();
			item.put("name", "order_id");
			item.put("content", order.getId());
			stb.getGlobalVars().add(item);

			item = new HashMap<String, Object>();
			item.put("name", "order_date");
			item.put("content", DateFormat.getDateInstance(DateFormat.SHORT, locale).format(order.getCreatedAt()));
			stb.getGlobalVars().add(item);
		}

		HashMap<String, Object> lines = new HashMap<String, Object>();
		lines.put("name", "lines");
		List<Object> tmp = new ArrayList<Object>();

		stb.addRecipient(customer.getEmail(), null, "to");

		String bccOrder = bcc.get(MessageType.ORDER);
		if (bccOrder != null && !bccOrder.isEmpty()) {
			for (String bccMail : bccOrder.split(",")) {
				stb.addRecipient(bccMail, null, "bcc");
			}
		}

		NumberFormat formatter = NumberFormat.getNumberInstance(locale);
		formatter.setMinimumFractionDigits(2);
		formatter.setMaximumFractionDigits(2);

		Currency currency = Currency.getInstance(order.getCurrencyCode());

		BigDecimal productTotal = BigDecimal.ZERO;
		List<String> qualifiers = Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT);
		for (OrderItem orderItem : order.getItems()) {
			if (qualifiers.contains(orderItem.getQualifier())) {
				productTotal = productTotal.add(orderItem.getTotalPrice("std"));
			}
		}

		BigDecimal shippingTotal = BigDecimal.ZERO;
		qualifiers = Arrays.asList(CartItemQualifier.SHIPPING);
		for (OrderItem orderItem : order.getItems()) {
			if (qualifiers.contains(orderItem.getQualifier())) {
				shippingTotal = shippingTotal.add(orderItem.getTotalPrice("std"));
			}
		}

		qualifiers = Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT);
		for (OrderItem orderItem : order.getItems()) {
			if (qualifiers.contains(orderItem.getQualifier())) {
				if (orderItem.getQuantity() != null) {
					HashMap<String, Object> item = new HashMap<String, Object>();

					item.putAll(orderItem.getTitle());
					item.put("quantity", orderItem.getQuantity());
					item.put("gtin", orderItem.getGtin());

					item.put("total_price", String.format("%1s&nbsp;%2s", currency.getSymbol(), formatter.format(orderItem.getTotalPrice("std"))));
					item.put("unit_price", String.format("%1s&nbsp;%2s", currency.getSymbol(), formatter.format(orderItem.getUnitPrice("std"))));

					tmp.add(item);
				}
			}
		}
		lines.put("content", tmp);
		stb.getGlobalVars().add(lines);

		qualifiers = Arrays.asList(CartItemQualifier.DISCOUNT);
		BigDecimal discountTotal = BigDecimal.ZERO;
		for (OrderItem orderItem : order.getItems()) {
			if (qualifiers.contains(orderItem.getQualifier())) {
				discountTotal = discountTotal.add(orderItem.getTotalPrice());
			}
		}

		HashMap<String, Object> discount = new HashMap<String, Object>();
		discount.put("name", "discount_total");
		discount.put("content", order.getCurrency() + " " + formatter.format(discountTotal));
		stb.getGlobalVars().add(discount);
		

		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put("name", "shipping_address_name");
		item.put("content", order.getShippingAddress().getFirstName() + " " + order.getShippingAddress().getLastName());
		stb.getGlobalVars().add(item);

		if (order.getShippingAddress().getCompany() != null) {
			item = new HashMap<String, Object>();
			item.put("name", "shipping_address_line_0");
			item.put("content", order.getShippingAddress().getCompany());
			stb.getGlobalVars().add(item);
		}

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_1");
		item.put("content", order.getShippingAddress().getStreet1() + " " + order.getShippingAddress().getStreet2());
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_2");
		String shippingAddressLine2Content = order.getShippingAddress().getPostcode().concat(" ").concat(order.getShippingAddress().getCity());
		if (StringUtils.isNotBlank(order.getShippingAddress().getRegion())) {
			shippingAddressLine2Content = shippingAddressLine2Content.concat(", ").concat(order.getShippingAddress().getRegion());
		}
		item.put("content", shippingAddressLine2Content);
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_3");
		if (countryCodes.containsKey(order.getShippingAddress().getCountryCode())) {
			item.put("content", countryCodes.get(order.getShippingAddress().getCountryCode()));
		} else {
			item.put("content", order.getShippingAddress().getCountryCode());
		}
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_4");
		item.put("content", order.getShippingAddress().getTelephone());
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "billing_address_name");
		item.put("content", order.getBillingAddress().getFirstName() + " " + order.getBillingAddress().getLastName());
		stb.getGlobalVars().add(item);

		if (order.getBillingAddress().getCompany() != null) {
			item = new HashMap<String, Object>();
			item.put("name", "billing_address_line_0");
			item.put("content", order.getBillingAddress().getCompany());
			stb.getGlobalVars().add(item);
		}

		item = new HashMap<String, Object>();
		item.put("name", "billing_address_line_1");
		item.put("content", order.getBillingAddress().getStreet1() + " " + order.getBillingAddress().getStreet2());
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "billing_address_line_2");
		String billingAddressLine2Content = order.getBillingAddress().getPostcode().concat(" ").concat(order.getBillingAddress().getCity());
		if (StringUtils.isNotBlank(order.getBillingAddress().getRegion())) {
			billingAddressLine2Content = billingAddressLine2Content.concat(", ").concat(order.getBillingAddress().getRegion());
		}
		item.put("content", billingAddressLine2Content);
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "billing_address_line_3");
		if (countryCodes.containsKey(order.getBillingAddress().getCountryCode())) {
			item.put("content", countryCodes.get(order.getBillingAddress().getCountryCode()));
		} else {
			item.put("content", order.getBillingAddress().getCountryCode());
		}
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "billing_address_line_4");
		item.put("content", order.getBillingAddress().getTelephone());
		stb.getGlobalVars().add(item);

		if (order.getBillingAddress().getTelephone() != null) {
			item = new HashMap<String, Object>();
			item.put("name", "customer_phone");
			item.put("content", order.getBillingAddress().getTelephone());
			stb.getGlobalVars().add(item);
		} else if (order.getShippingAddress().getTelephone() != null) {
			item = new HashMap<String, Object>();
			item.put("name", "customer_phone");
			item.put("content", order.getShippingAddress().getTelephone());
			stb.getGlobalVars().add(item);
		}

		item = new HashMap<String, Object>();
		item.put("name", "order_id");
		item.put("content", order.getId());
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "payment_mehtod");
		item.put("content", "PayPal");
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "product_total");
		item.put("content", String.format("%1s&nbsp;%2s", currency.getSymbol(), formatter.format(productTotal)));
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_total");
		item.put("content", String.format("%1s&nbsp;%2s", currency.getSymbol(), formatter.format(shippingTotal)));
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "order_total");
		item.put("content", String.format("%1s&nbsp;%2s", currency.getSymbol(), formatter.format(order.getTotal(true).getGross())));
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		List<Object> content = new ArrayList<Object>();
		item.put("name", "vat");
		item.put("content", content);
		for (Entry<TaxCode, ? extends Total.Line> entry : order.getTotal(true).getLines().entrySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taxPercent", entry.getValue().getTaxPercent());
			map.put("tax", String.format("%1s&nbsp;%2s", currency.getSymbol(), formatter.format(entry.getValue().getTax())));
			content.add(map);
		}
		stb.getGlobalVars().add(item);

		return stc;
	}

	private Call buildDispatchedMailCall(Map<String, Object> data) throws ComConnectorException {
		SendTemplateCall stc = new SendTemplateCall();
		SendTemplateBody stb = stc.getSendTemplateBody();

		Order order = (Order) data.get("order");
		stb.setTemplateName(templateResolver.getTemplate(MessageType.DISPATCHED, (String) data.get(DataMapKeys.SHOP_CODE)));

		// TODO: get locale from order ----
		Locale locale = Locale.GERMANY;
		if ("PUMPKIN_COM".equals(order.getShopCode())) {
			locale = Locale.UK;
		}

		// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMMM
		// yyyy");

		String subject = subjects.get(MessageType.DISPATCHED + "-" + (String) data.get(DataMapKeys.SHOP_CODE));

		// MessageFormat.format(subject, order.getId(),
		// simpleDateFormat.format(order.getCreatedAt()));

		stb.setSubject(MessageFormat.format(subject, order.getId(), DateFormat.getDateInstance(DateFormat.SHORT, locale).format(order.getCreatedAt())));
		Customer customer = (Customer) data.get("customer");
		
		stb.addRecipient(customer.getEmail(), null, "to");

		String bccOrder = bcc.get(MessageType.ORDER);
		if (bccOrder != null && !bccOrder.isEmpty()) {
			for (String bccMail : bccOrder.split(",")) {
				stb.addRecipient(bccMail, null, "bcc");
			}
		}

		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put("name", "shipping_provider");
		item.put("content", "DHL");
		stb.getGlobalVars().add(item);
		
		item = new HashMap<String, Object>();
		item.put("name", "tracking_number");
		item.put("content", order.getPackets().get(0).getTrackingId());
		stb.getGlobalVars().add(item);
		
		item = new HashMap<String, Object>();
		item.put("name", "tracking_link");
		item.put("content", "https://nolp.dhl.de/nextt-online-public/de/search?piececode="+order.getPackets().get(0).getTrackingId());
		stb.getGlobalVars().add(item);
		
		item = new HashMap<String, Object>();
		item.put("name", "first_name");
		item.put("content", customer.getFirstName());
		stb.getGlobalVars().add(item);
		
		item = new HashMap<String, Object>();
		item.put("name", "customer_id");
		item.put("content", order.getCustomerId());
		stb.getGlobalVars().add(item);
		
		item = new HashMap<String, Object>();
		item.put("name", "order_id");
		item.put("content", order.getId());
		stb.getGlobalVars().add(item);
		
		item = new HashMap<String, Object>();
		item.put("name", "order_date");
		item.put("content", DateFormat.getDateInstance(DateFormat.SHORT, locale).format(order.getCreatedAt()));
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_name");
		item.put("content", order.getShippingAddress().getFirstName() + " " + order.getShippingAddress().getLastName());
		stb.getGlobalVars().add(item);
		
		if (order.getShippingAddress().getCompany() != null) {
			item = new HashMap<String, Object>();
			item.put("name", "shipping_address_line_0");
			item.put("content", order.getShippingAddress().getCompany());
			stb.getGlobalVars().add(item);
		}

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_1");
		item.put("content", order.getShippingAddress().getStreet1() + " " + order.getShippingAddress().getStreet2());
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_2");
		item.put("content", order.getShippingAddress().getPostcode() + " " + order.getShippingAddress().getCity());
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_3");
		if (countryCodes.containsKey(order.getShippingAddress().getCountryCode())) {
			item.put("content", countryCodes.get(order.getShippingAddress().getCountryCode()));
		} else {
			item.put("content", order.getShippingAddress().getCountryCode());
		}
		stb.getGlobalVars().add(item);

		item = new HashMap<String, Object>();
		item.put("name", "shipping_address_line_4");
		item.put("content", order.getShippingAddress().getTelephone());
		stb.getGlobalVars().add(item);

		return stc;
	}

	public TemplateResolver getTemplateResolver() {
		return templateResolver;
	}

	public void setTemplateResolver(TemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
	}

	@Override
	public Map<String, String> getBcc() {
		return bcc;
	}

	@Override
	public void setBcc(Map<String, String> bcc) {
		this.bcc = bcc;
	}
}
