package com.acl.enterprise.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class EnterpriseService {
	private static final Logger logger=LoggerFactory.getLogger(EnterpriseService.class);
	@Autowired
	private RestTemplate restTemplate;
	@Retryable(maxAttempts = 1, value = RuntimeException.class, backoff = @Backoff(delay = 1500, multiplier = 2))
	public String callEnterpriseService(String serviceUrl, String data) {
		String response=null;
		try {
			 logger.info("Service URL"+ serviceUrl);
			ResponseEntity<String>  responseEnt=restTemplate.postForEntity(serviceUrl, data, String.class);
			response=responseEnt.getBody();
			logger.info("Response : "+response);
		 
		}catch (RestClientException e3) {
			logger.error("errer are coming in enterprise service call", e3);
			e3.printStackTrace();
			throw e3;
		}
		catch (Exception e) {
			logger.error("errer are coming in enterprise service call", e);
			e.printStackTrace();
			throw e;
		}
		return response;
	}
}
