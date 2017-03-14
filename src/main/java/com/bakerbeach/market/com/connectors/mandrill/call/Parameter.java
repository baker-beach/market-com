package com.bakerbeach.market.com.connectors.mandrill.call;

import java.util.HashMap;

public class Parameter extends HashMap<String,Object>{

	private static final long serialVersionUID = 1L;
	
	public String getName(){
		return (String)get("name");
	}
	
	public void setName(String name){
		put("name",name);
	}
	
	public String getContent(){
		return (String)get("content");
	}
	
	public void setContent(String content){
		put("content",content);
	}
}
