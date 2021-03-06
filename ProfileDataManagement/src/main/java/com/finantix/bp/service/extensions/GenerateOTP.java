package com.finantix.bp.service.extensions;

import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;




import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.rest.RESTServiceException;
import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.drools.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class GenerateOTP extends RESTWorkItemHandler {
	
	private static final String AUTHORIZE = " Use it to authorize the change";
	private static final String AUTHORIZATION_CODE = "Authorization code:";
	private static final String METHOD = "Method";
	private static final String URL = "Url";
	private static final String CONTENT_TYPE = "ContentType";
	private static final String APPLICATION_JSON = "application/json";
	private static final String HEADERS = "Headers";
	private static final String CONTENT_DATA = "ContentData";
	private static final String FTX_HEADER = "ContentType=application/jsonAccept=application/json;Content-Type=application/json;X-Requested-With=XmlHttpRequest";
	private static final String POST = "POST";
	private static final String NUMBER = "number";
	private static final String ORG = "org";
	private static final String TENANTID = "tenantid";
	private static final String USEREMAIL = "useremail";
	private static final String PASSWORD = "password";
	private static final String USER2 = "user";
	private static final String SERVER = "server";
	private static final String GENERATE_OTP = "FTX GenerateOTP v1.0.1";
	private static final String HTTPS_REST_NEXMO_COM_SMS_JSON = "https://rest.nexmo.com/sms/json";
	private static final String RESULTS_VALUE = "OTP";
	String host = "";
	String serverURL = "/rest/otp/generate/";
	String user = "admin@thedigitalstack.com";
	String password = PASSWORD;
	private static final Logger logger = LoggerFactory.getLogger(GenerateOTP.class);
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		

		logger.error(GENERATE_OTP);
		WorkItemImpl customworkItem = new WorkItemImpl();

		String useremail = "";
		String tenantid = "";
		String from = "finantix";
		String number = "6591052920";

		String jsonString = "";
		com.finantix.bp.service.extensions.Util util = new com.finantix.bp.service.extensions.Util();
		try {

			host = util.getPropValue(SERVER);
			user = util.getPropValue(USER2);
			password = util.getPropValue(PASSWORD);
			useremail = util.getPropValue(USEREMAIL);
			tenantid = util.getPropValue(TENANTID);

			from = util.getPropValue(ORG);
			number = util.getPropValue(NUMBER);

			jsonString = "{\"tenant\":\"" + tenantid + "\",\"username\":\"" + user + "\",\"email\":\"" + useremail
					+ "\",\"locale\":\"en\"}";

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Map<String, Object> orignalparams = workItem.getParameters();
		logger.error("executeWorkItem orignalparams : " + orignalparams);
		

		customworkItem.setParameters(orignalparams);
		customworkItem.setParameter(Constants.AUTH_TYPE, Constants.BASIC);
		customworkItem.setParameter(Constants.LOGIN, user);
		customworkItem.setParameter(Constants.LPASS, password);
		customworkItem.setParameter(CONTENT_TYPE, APPLICATION_JSON);
		customworkItem.setParameter(URL, host + serverURL);
		customworkItem.setParameter(METHOD, POST);

		ObjectMapper mapper = new ObjectMapper();

		customworkItem.setParameter(CONTENT_DATA, jsonString);

		customworkItem.setParameter(HEADERS, FTX_HEADER);

		logger.error("executeWorkItem getParameters : " + customworkItem.getParameters());

		super.executeWorkItem(customworkItem, manager);

		customworkItem.setParameter(CONTENT_DATA, jsonString);

		customworkItem.setParameter(HEADERS, FTX_HEADER);

		logger.error("executeWorkItem getParameters : " + customworkItem.getParameters());

		super.executeWorkItem(customworkItem, manager);

		String OTP = Util.generatorOTP(4);

		String message = AUTHORIZATION_CODE + OTP + AUTHORIZE;

		jsonString = "{\"from\": \"" + from + "\",\"text\": \"" + message + "\",\"to\": \"" + number
				+ "\",\"api_key\": \"e54ec738\",\"api_secret\": \"2c10be8a\"}";
		customworkItem.setParameter(CONTENT_DATA, jsonString);
		customworkItem.setParameter(CONTENT_TYPE, APPLICATION_JSON);
		customworkItem.setParameter(URL, HTTPS_REST_NEXMO_COM_SMS_JSON);
		customworkItem.setParameter(METHOD, POST);
		logger.error("executeWorkItem 2 getParameters : " + customworkItem.getParameters());
		super.executeWorkItem(customworkItem, manager);
		logger.error("executeWorkItem 2 DONE : " + results);
		results.put(RESULTS_VALUE, OTP);

		logger.error("executeWorkItem 2 DONE : " + results);
		manager.completeWorkItem(workItem.getId(), results);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		super.abortWorkItem(workItem, manager);
	}

	private ClassLoader classLoader;

	Map<String, Object> results = null;

	@Override
	protected void postProcessResult(String result, String resultClass, String contentType,
			Map<String, Object> results) {

		logger.error("postProcessResult  result: " + result);

		if (!StringUtils.isEmpty(resultClass) && !StringUtils.isEmpty(contentType)) {
			try {
				Class<?> clazz = Class.forName(resultClass, true, classLoader);

				logger.error("postProcessResult executeWorkItem  result: " + result + "clazz:" + clazz);

				Object resultObject = transformResult(clazz, contentType, result);

				results.put(PARAM_RESULT, resultObject);
			} catch (Throwable e) {
				throw new RuntimeException("Unable to transform respose to object", e);
			}
		} else {

			results.put(PARAM_RESULT, result);
		}
		logger.error("postProcessResult executeWorkItem  results: " + results);
		this.results = results;

	}

	

}
