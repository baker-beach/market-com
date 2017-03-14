package com.bakerbeach.market.com.connectors.mandrill.call.body;

import java.util.HashMap;

public class Body extends HashMap<String,Object>{
	
	private static final long serialVersionUID = 1L;

	public String getKey() {
		return (String)get("key");
	}

	public void setKey(String key) {
		put("key",key);
	}

}
