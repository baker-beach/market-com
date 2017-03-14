package com.bakerbeach.market.com.connectors.mandrill.call.body;

public class AddTemplateBody extends Body{

	private static final long serialVersionUID = 8354701892276804218L;
	
	public String getName(){
		return (String)get("name");
	}
	
	public void setName(String name){
		put("name",name);
	}
	
	public String getFromEmail(){
		return (String)get("from_email");
	}
	
	public void setFromEmail(String name){
		put("from_email",name);
	}
	
	public String getFromName(){
		return (String)get("from_name");
	}
	
	public void setFromName(String name){
		put("from_name",name);
	}
	
	public String getSubject(){
		return (String)get("subject");
	}
	
	public void setSubject(String name){
		put("subject",name);
	}
	
	public String getCode(){
		return (String)get("code");
	}
	
	public void setCode(String name){
		put("code",name);
	}
	
	public String getText(){
		return (String)get("text");
	}
	
	public void setText(String name){
		put("text",name);
	}
	
	public Boolean getPublish(){
		return (Boolean)get("publish");
	}
	
	public void setPublish(Boolean publish){
		put("publish",publish);
	}
	
	public AddTemplateBody(){
		setName("Order");
		setFromEmail("from_email@example.com");
		setFromName("Shop");
		setSubject("Order mail");
		setPublish(false);
		setCode("");
		setText("");
	}
}
