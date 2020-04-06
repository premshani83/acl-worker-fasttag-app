package com.acl.executor;

import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acl.AppUtils;
import com.acl.enterprise.service.EnterpriseService;
import com.acl.enterprise.service.SMSPush;
import com.acl.model.MSDNDetailsDTO;
import com.acl.pgp.security.PGPSecurityUtils;
import com.acl.repository.MsisdnService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Worker implements Callable<MSDNDetailsDTO> {

	private static final Logger logger = LoggerFactory.getLogger(Worker.class);

	private String serviceUrl;
	private String merchentId;
	private String merchentKey;
	private String message;
	private int firstRetryInterval;
	private int secondRetryInterval;
	private int thiredRetryInterval;

	private MSDNDetailsDTO job;
	private MsisdnService msisdnService;
	private PGPSecurityUtils pgpSecureUtils;
	private SMSPush smsPush;
	private EnterpriseService enterpriseService;
	private String successRespTemplate;
	private String errorRespTemplate;
	private int retryEncounter;

	public Worker(MSDNDetailsDTO job, String serviceUrl, String merchentId, String merchentKey, String message,
			int firstRetryInterval, int secondRetryInterval, int thiredRetryInterval, MsisdnService msisdnService,
			PGPSecurityUtils pgpSecureUtils, SMSPush smsPush, EnterpriseService enterpriseService, 
			String successResTemp, String errorResTemp, int retryEncounter) {
		this.job = job;
		this.serviceUrl = serviceUrl;
		this.merchentId = merchentId;
		this.merchentKey = merchentKey;
		this.message = message;
		this.firstRetryInterval = firstRetryInterval;
		this.secondRetryInterval = secondRetryInterval;
		this.thiredRetryInterval = thiredRetryInterval;
		this.msisdnService = msisdnService;
		this.pgpSecureUtils = pgpSecureUtils;
		this.smsPush = smsPush;
		this.enterpriseService = enterpriseService;
		this.successRespTemplate = successResTemp;
		this.errorRespTemplate = errorResTemp;
		this.retryEncounter=retryEncounter;
	}

	@Override
	public MSDNDetailsDTO call() throws Exception {
		process();
		String message = String.format("CallableWorker name: %s is Done", AppUtils.maskString(job.getMsisdn(), 0, "X"));
		return job;
	}

	private void process() {
		String smsBody="";
		String msgLog="";
		try {
			JsonObject request = new JsonObject();
			request.addProperty("merchantId", merchentId);
			request.addProperty("merchantKey", merchentKey);
			request.addProperty("mobileNo", AppUtils.maskString(job.getMsisdn(), 0, "X"));
			request.addProperty("message", message);
			logger.info("request is made :" + request.toString());
			request.addProperty("mobileNo", job.getMsisdn());
			String encryptedData = pgpSecureUtils.base64Encode(request.toString());
			String response = enterpriseService.callEnterpriseService(serviceUrl, encryptedData);
			String decryptedMessage = pgpSecureUtils.base64Decode(response);
			logger.info("Response from Enterprise : " + decryptedMessage.replaceAll("A/c no (.*)", "XXXXXXXXXXXXX"));
			JsonObject josnResponse = new JsonParser().parse(decryptedMessage).getAsJsonObject();
			if("00".equals(josnResponse.get("statusCode").getAsString())) {
				String statusDesc=josnResponse.get("SMS").getAsString();
				String accNo= statusDesc.split(" ")[10];
				String ballanceInr= statusDesc.split(" ")[13];
				msgLog=successRespTemplate;
				msgLog=msgLog.replaceFirst("\\$accountno", AppUtils.maskString(accNo, 0, "X"));
				msgLog=msgLog.replaceFirst("\\$inr", AppUtils.maskInr(ballanceInr, 0, "X") );
				logger.info("Success Response Template : "+msgLog);				
				successRespTemplate=successRespTemplate.replaceFirst("\\$accountno", accNo);
				successRespTemplate=successRespTemplate.replaceFirst("\\$inr", ballanceInr);
				smsBody=successRespTemplate;
				
				//logger.info("Success Response Template : "+successRespTemplate);
			}else {
				logger.info("Error comes from enterprise : sms response template will be : "+errorRespTemplate);
				smsBody=errorRespTemplate;
			}
			
			smsPush.sendSMS(job.getMsisdn(), smsBody, msgLog);
			logger.info("proccesed " + decryptedMessage.replaceAll("A/c no (.*)", "XXXXXXXXXXXXX"));
		
		}
		catch (Exception e) {
			// if any exception occur then store into retry queue with timestamp
			// time stamp as key with incremental by minutes
			logger.error("error ", e);
			try {
				if ( job.getRetryCount() < retryEncounter ) {
					String incrementedTimeStamp = null;
					if (job.getRetryCount() == 0) {
						incrementedTimeStamp = AppUtils.getIncementdTimeStamp(firstRetryInterval);
					} else if (job.getRetryCount() == 1) {
						incrementedTimeStamp = AppUtils.getIncementdTimeStamp(secondRetryInterval);
					} else if (job.getRetryCount() == 2) {
						incrementedTimeStamp = AppUtils.getIncementdTimeStamp(thiredRetryInterval);
					}else {
						incrementedTimeStamp = AppUtils.getIncementdTimeStamp(thiredRetryInterval);
					}
					job.setRetryCount(job.getRetryCount() + 1);
					job.setUpdatDate(new Date());
					
						System.out.println("starting save ");
						msisdnService.saveRetry(incrementedTimeStamp, job);
						System.out.println("ending save ");
					
					
					logger.info("added into retry queue " + AppUtils.maskString(job.getMsisdn(), 0,  "X")+" Retry Count "+job.getRetryCount());
				} else {
					smsPush.sendSMS(job.getMsisdn(), errorRespTemplate);
					logger.info(
							"We have tried maximum time and send error message to orginated mobile: " + AppUtils.maskString(job.getMsisdn(), 0, "X") + "Retry Count " + job.getRetryCount());
				}
			} catch (Exception e1) {
				logger.error("error", e1);
			}

			System.out.println("Socket Time Exception is coming and job added into retry queue ");
		}

	}

}
