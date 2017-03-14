package com.bakerbeach.market.com.connectors.mandrill.call;

import com.bakerbeach.market.com.connectors.mandrill.call.body.Body;

public interface Call {
	
	String getPath();
	
	Body getBody(); 

}
