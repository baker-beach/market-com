package com.bakerbeach.market.com.connectors.smtp;

import java.util.Map;

public interface RenderService {
	
	String render(Map<String,Object> parameter, String type) throws RenderServiceException;

}
