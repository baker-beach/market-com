package com.bakerbeach.market.com.connectors.mandrill.call;

import com.bakerbeach.market.com.connectors.mandrill.call.body.AddTemplateBody;
import com.bakerbeach.market.com.connectors.mandrill.call.body.Body;

public class AddTemplateCall  implements Call {
	
	private AddTemplateBody body = new AddTemplateBody();

	@Override
	public String getPath() {
		return "/templates/update";
	}

	@Override
	public Body getBody() {
		return body;
	}
	
	public void setTemplateCode(String code){
		body.setCode(code);
	}

}