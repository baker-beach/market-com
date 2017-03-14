package com.bakerbeach.market.com.connectors.mandrill.call;

import com.bakerbeach.market.com.connectors.mandrill.call.body.Body;

public class TemplateListCall  implements Call {
	
	private Body body = new Body();

	@Override
	public String getPath() {
		return "/templates/list";
	}

	@Override
	public Body getBody() {
		return body;
	}

}