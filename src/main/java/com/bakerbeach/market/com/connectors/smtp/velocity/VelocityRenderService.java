package com.bakerbeach.market.com.connectors.smtp.velocity;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.bakerbeach.market.com.connectors.smtp.RenderService;
import com.bakerbeach.market.com.connectors.smtp.RenderServiceException;

public class VelocityRenderService implements RenderService {

	private VelocityEngine ve;
	private String templatePath;
	private Map<String, String> typeTemplateMap;

	public VelocityRenderService() {
		ve = new VelocityEngine();
		ve.setProperty("resource.loader", "class");
		ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init();
	}

	@Override
	public String render(Map<String, Object> parameter, String type) throws RenderServiceException {
		Template t = getTemplate(type);
		VelocityContext context = new VelocityContext();
		for (String key : parameter.keySet()) {
			context.put(key, parameter.get(key));
		}
		try {
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new RenderServiceException();
		}
	}

	private Template getTemplate(String type) throws RenderServiceException {
		String fileName = typeTemplateMap.get(type);
		if (fileName != null) {
			try {
				return ve.getTemplate(templatePath + fileName + ".vm", "UTF-8");
			} catch (ResourceNotFoundException | ParseErrorException e) {
				throw new RenderServiceException();
			}
		} else
			throw new RenderServiceException();
	}

	/**
	 * @return the templatePath
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * @param templatePath
	 *            the templatePath to set
	 */
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	/**
	 * @return the typeTemplateMap
	 */
	public Map<String, String> getTypeTemplateMap() {
		return typeTemplateMap;
	}

	/**
	 * @param typeTemplateMap
	 *            the typeTemplateMap to set
	 */
	public void setTypeTemplateMap(Map<String, String> typeTemplateMap) {
		this.typeTemplateMap = typeTemplateMap;
	}

}
