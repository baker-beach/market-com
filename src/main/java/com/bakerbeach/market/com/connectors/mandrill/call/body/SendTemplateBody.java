package com.bakerbeach.market.com.connectors.mandrill.call.body;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendTemplateBody extends Body {

	private static final long serialVersionUID = 1L;

	public SendTemplateBody() {
		put("template_content", new ArrayList<Object>());
		put("message", new HashMap<String, Object>());
		getMessage().put("global_merge_vars", new ArrayList<Object>());
		getMessage().put("merge_vars", new ArrayList<Object>());
		getMessage().put("to", new ArrayList<Object>());
		getMessage().put("merge", true);
		getMessage().put("important", false);
		getMessage().put("merge_language", "handlebars");
	}

	public String getTemplateName() {
		return (String) get("template_name");
	}

	public void setTemplateName(String templateName) {
		put("template_name", templateName);
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getMessage() {
		return (Map<String, Object>) get("message");
	}

	@SuppressWarnings("unchecked")
	public List<Object> getGlobalVars() {
		return (List<Object>) getMessage().get("global_merge_vars");
	}

	public String getSubject() {
		return (String) getMessage().get("subject");
	}

	public void setSubject(String subject) {
		getMessage().put("subject", subject);
	}

	@SuppressWarnings("unchecked")
	public void addRecipient(String email, String name, String type) {
		if (email != null && !email.isEmpty()) {
			Map<String, String> recipient = new HashMap<String, String>();
			recipient.put("email", email);
			if (name != null) {
				recipient.put("name", name);
			}
			if (type != null) {
				recipient.put("type", type);
			}
			((List<Object>) getMessage().get("to")).add(recipient);
		}
	}

}
