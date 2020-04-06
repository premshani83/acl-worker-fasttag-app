package com.acl;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application constant file which have the all constant 
 * All constant load from the properties file
 * @author prem.sahani
 *
 */
@Component
public class AppConstant {

	
	@Value("com.acl.new.batch.size")
	private String  batchNewSize;
	@Value("com.acl.retry.batch.size")
	private String retryBatchSize;
	@Value("com.acl.enterprise.service.url")
	private String serviceUrl;
	@Value("com.acl.sms.push.service.url")
	private String serviceSmsUrl;
	@Value("com.acl.enterprise.merchent.id")
	private String merchentId;
	@Value("com.acl.enterprise.merchent.key")
	private String merchentKey;

	@Value("com.acl.enterprise.message")
	private String message;

	@Value("com.acl.enterprise.first.retry.interval.in.minut")
	private String firstRetryInterval;

	@Value("com.acl.enterprise.second.retry.interval.in.minut")
	private String secondRetryInterval;

	@Value("com.acl.enterprise.thired.retry.interval.in.minut")
	private String thiredRetryInterval;
	
	@Value("redis.host")
	private String redisHost;
	@Value("redis.port")
	private String redisPort;
	@Value("redis.password")
	private String redisPassword;
	
	@Value("threadpool.corepoolsize")
    String corePoolSize;
     
    @Value("threadpool.maxpoolsize")
    String maxPoolSize;
	
    @Value("com.acl.public.key.file.loc")
	private String publicKeyFileLoc;
	@Value("com.acl.private.key.file.loc")
	private String privateKeyFileLoc;
	@Value("com.acl.private.key.password")
	private String privateKeyPassword;
	
	
	@Value("com.acl.new.queue")
	private String newQueue;
	
    @Value("com.acl.new.retry")
    private String retryQueue;
    
	@Value("com.acl.sms.success.response.template")
	private String successResTemplate;
	@Value("com.acl.sms.error.response.template")
	private String errorResTemplate;
	@Value("com.acl.max.number.retry")		
	private String retryEncounter;
	@Autowired
	private PropertiesConfiguration proConfg;

	public int getBatchNewSize() {
		return proConfg.getInt(batchNewSize);
	}

	
	public int getRetryBatchSize() {
		return proConfg.getInt(retryBatchSize);
	}

	
	public String getServiceUrl() {
		return proConfg.getString(serviceUrl);
	}

	
	public String getMerchentId() {
		return proConfg.getString(merchentId);
	}

	

	public String getMerchentKey() {
		return proConfg.getString(merchentKey);
	}

	
	public String getMessage() {
		return proConfg.getString(message);
	}

	

	public int getFirstRetryInterval() {
		return proConfg.getInt(firstRetryInterval);
	}

		public int getSecondRetryInterval() {
		return proConfg.getInt(secondRetryInterval);
	}

	
	public int getThiredRetryInterval() {
		return proConfg.getInt(thiredRetryInterval);
	}


	public String getRedisHost() {
		return proConfg.getString(redisHost);
	}


	

	public Integer getRedisPort() {
		return proConfg.getInt(redisPort);
	}


	
	public String getRedisPassword() {
		return proConfg.getString(redisPassword);
	}


	public String getServiceSmsUrl() {
		return proConfg.getString(serviceSmsUrl);
	}


	public int getCorePoolSize() {
		return proConfg.getInt(corePoolSize);
	}


	


	public int getMaxPoolSize() {
		return proConfg.getInt(maxPoolSize);
	}


	public String getPublicKeyFileLoc() {
		return proConfg.getString(publicKeyFileLoc);
	}


	
	public String getPrivateKeyFileLoc() {
		return proConfg.getString(privateKeyFileLoc);
	}


	

	public String getPrivateKeyPassword() {
		return proConfg.getString(privateKeyPassword);
	}


	public String getNewQueue() {
		return proConfg.getString(newQueue);
	}


	

	public String getRetryQueue() {
		return proConfg.getString(retryQueue);
	}


	public String getSuccessResTemplate() {
		
		return StringUtils.join(proConfg.getStringArray(successResTemplate),", ");
	}




	public String getErrorResTemplate() {
		return StringUtils.join(proConfg.getStringArray(errorResTemplate), ", ");
	}


	public int getRetryEncounter() {
		return proConfg.getInt(retryEncounter);
	}


	


	
	
}
