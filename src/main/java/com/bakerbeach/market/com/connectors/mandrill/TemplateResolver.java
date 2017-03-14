package com.bakerbeach.market.com.connectors.mandrill;

import com.bakerbeach.market.com.api.ComConnectorException;
import com.bakerbeach.market.com.api.MessageType;

public class TemplateResolver {
	
	public String getTemplate(String messageType, String shopCode) throws ComConnectorException{
		if(MessageType.WELCOME.equals(messageType)){
			return "welcome";
		} else if (MessageType.PASSWORD.equals(messageType)){
			return "password";
		}else if (MessageType.ORDER.equals(messageType)){
			return "order";
		}else if (MessageType.DISPATCHED.equals(messageType)){
			return "dispatched";
		}else
			throw new ComConnectorException();
	}
}
