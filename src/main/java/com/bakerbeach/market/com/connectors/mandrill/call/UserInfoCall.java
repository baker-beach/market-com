package com.bakerbeach.market.com.connectors.mandrill.call;

import com.bakerbeach.market.com.connectors.mandrill.call.body.Body;

public class UserInfoCall implements Call {
	
	private Body body = new Body();

	@Override
	public String getPath() {
		return "/users/info";
	}

	@Override
	public Body getBody() {
		return body;
	}

}
