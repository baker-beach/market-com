package com.bakerbeach.market.integration.handler;

import java.util.HashMap;
import java.util.Map;

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

public class CustomerEventHandler extends AbstractHandler {

	final Logger log = LoggerFactory.getLogger(CustomerEventHandler.class);

	private ComConnector comConnector;

	private CustomerService customerService;

	public void welcome(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();

			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, payload.get("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for welcome mail");
			}

			if (payload.containsKey("customer_id")) {

				Customer customer = customerService.findById((String) payload.get("customer_id"));
				if (customer != null) {
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException("customer not found with id " + (String) payload.get("customer_id"));
				}
			} else {
				throw new EventHandlerException("missing customer_id parameter for welcome mail");
			}
			comConnector.generateMessageAndSend(MessageType.WELCOME, data);
		} catch (EventHandlerException | CustomerServiceException | ComConnectorException e) {
			log.error(e.getMessage());
			log.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	public void password(Exchange ex) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = (Map<String, Object>) getPayload(ex.getIn());
			Map<String, Object> data = new HashMap<String, Object>();

			if (payload.containsKey("shop_code")) {
				data.put(DataMapKeys.SHOP_CODE, payload.get("shop_code"));
			} else {
				throw new EventHandlerException("missing shop_code parameter for password mail");
			}

			if (payload.containsKey("password")) {
				data.put(DataMapKeys.PASSWORD, payload.get("password"));
			} else {
				throw new EventHandlerException("missing password parameter for password mail");
			}

			if (payload.containsKey("customer_id")) {

				Customer customer = customerService.findById((String) payload.get("customer_id"));
				if (customer != null) {
					data.put(DataMapKeys.CUSTOMER, customer);
					data.put(DataMapKeys.RECIPIENT, customer.getEmail());
				} else {
					throw new EventHandlerException("customer not found with id " + (String) payload.get("customer_id") + " for password mail");
				}
			} else {
				throw new EventHandlerException("missing customer_id parameter for password mail");
			}
			comConnector.generateMessageAndSend(MessageType.PASSWORD, data);
		} catch (EventHandlerException | CustomerServiceException | ComConnectorException e) {
			log.error(e.getMessage());
			log.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	public ComConnector getComConnector() {
		return comConnector;
	}

	public void setComConnector(ComConnector comConnector) {
		this.comConnector = comConnector;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

}
