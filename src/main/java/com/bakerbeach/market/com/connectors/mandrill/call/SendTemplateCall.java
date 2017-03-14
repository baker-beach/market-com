package com.bakerbeach.market.com.connectors.mandrill.call;

import com.bakerbeach.market.com.connectors.mandrill.call.body.Body;
import com.bakerbeach.market.com.connectors.mandrill.call.body.SendTemplateBody;

public class SendTemplateCall  implements Call {
	
	private SendTemplateBody body = new SendTemplateBody();

	@Override
	public String getPath() {
		return "/messages/send-template";
	}

	@Override
	public Body getBody() {
		return body;
	}
	
	public SendTemplateBody getSendTemplateBody() {
		return body;
	}
	
}