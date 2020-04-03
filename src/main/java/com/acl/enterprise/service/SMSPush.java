package com.acl.enterprise.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.acl.AppConstant;
import com.acl.AppUtils;

@Component
public class SMSPush {

	private static final Logger logger=LoggerFactory.getLogger(SMSPush.class);
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private AppConstant appConstant;
	public void sendSMS(String mobileNo, String text) {
		sendSMS(mobileNo,  text, null);
	}
	public void sendSMS(String mobileNo, String text, String msgLog) {
		try {
			//Send the sms to the originated mobile.
			StringBuilder addParam=new StringBuilder(appConstant.getServiceSmsUrl());
			logger.info("URL : "+addParam);
			addParam.append("&to="+mobileNo.trim());
			addParam.append("&text="+text);
		    ResponseEntity<String>	resp=restTemplate.getForEntity(addParam.toString(), String.class);
		    logger.info("Response from push api: "+resp.getBody());
		    if(msgLog==null) {
		    	msgLog=text;
		    }
		    logger.info("message send to originated mobile : "+AppUtils.maskString(mobileNo, 0, "X") + " text: "+msgLog);
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception comes into send sms", e);
		}
		
	}
	
}
