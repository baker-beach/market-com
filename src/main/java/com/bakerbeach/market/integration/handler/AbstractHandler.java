package com.bakerbeach.market.integration.handler;

import java.io.IOException;
import java.util.Map;

import org.apache.camel.Message;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class AbstractHandler {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map getPayload(Message message) throws EventHandlerException {
		try {
			if (message.getBody() instanceof String) {
				String payloadAsString = (String) message.getBody();
				if (payloadAsString != null && !payloadAsString.isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> map = (Map<String, Object>) mapper.readValue(payloadAsString, Map.class);
					message.setBody(map);
				} else {
					throw new EventHandlerException();
				}
			}

			Map<String, Object> payload = (Map<String, Object>) message.getBody();

			return payload;
		} catch (IOException e) {
			throw new EventHandlerException(e);
		}
	}	
}