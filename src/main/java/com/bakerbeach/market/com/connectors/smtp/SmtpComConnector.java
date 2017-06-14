package com.bakerbeach.market.com.connectors.smtp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.bakerbeach.market.com.api.ComConnector;
import com.bakerbeach.market.com.api.ComConnectorException;
import com.bakerbeach.market.com.api.DataMapKeys;
import com.bakerbeach.market.com.connectors.smtp.velocity.Formater;
import com.bakerbeach.market.com.connectors.smtp.velocity.OrderContextHelper;
import com.bakerbeach.market.order.api.model.Order;

public class SmtpComConnector implements ComConnector {

	private Map<String, String> bcc = new HashMap<String, String>();

	private Properties properties;

	private RenderService renderService;

	@Override
	public void generateMessageAndSend(String messageType, Map<String, Object> dataMap) throws ComConnectorException {

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication((String) properties.get("mail.smtp.user"),
						(String) properties.get("mail.smtp.password"));
			}
		};

		Session session = Session.getDefaultInstance(properties, auth);

		try {
			dataMap.put("formater", new Formater());
			
			if(dataMap.containsKey("order")){
				Map<String, Object> params = OrderContextHelper.buildRenderContext((Order)dataMap.get("order"));
				for(String key : params.keySet()){
					dataMap.put(key, params.get(key));
				}
			}
			
			Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress((String) properties.get("mail.smtp.sender")));
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse((String)dataMap.get(DataMapKeys.RECIPIENT), false));

			if (bcc.containsKey(messageType))
				msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc.get(messageType), false));

			msg.setSubject(renderService.render(dataMap, messageType+"_subject"));
			msg.setContent(renderService.render(dataMap, messageType), "text/html; charset=utf-8");

			msg.setSentDate(new Date());

			Transport.send(msg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Map<String, String> getBcc() {
		return bcc;
	}

	@Override
	public void setBcc(Map<String, String> bcc) {
		this.bcc = bcc;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public RenderService getRenderService() {
		return renderService;
	}

	public void setRenderService(RenderService renderService) {
		this.renderService = renderService;
	}

}
