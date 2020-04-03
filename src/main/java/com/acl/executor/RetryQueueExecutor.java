package com.acl.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.acl.AppConstant;
import com.acl.AppUtils;
import com.acl.enterprise.service.EnterpriseService;
import com.acl.enterprise.service.SMSPush;
import com.acl.model.MSDNDetailsDTO;
import com.acl.pgp.security.PGPSecurityUtils;
import com.acl.repository.MsisdnService;

@Component
public class RetryQueueExecutor {
	private static final Logger logger = LoggerFactory.getLogger(RetryQueueExecutor.class);
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolRetry;

	@Autowired
	private MsisdnService msisdnService;
	@Autowired
	private PGPSecurityUtils pgpSecureUtils;
	@Autowired
	private SMSPush smsPush;
	@Autowired
	private EnterpriseService enterpriseService;
	
	@Autowired
	private AppConstant appConstant;

	@Async
	@Scheduled(fixedRate = 1000)
	public void startRetryProcessor() throws Exception {
		System.out.println("start Retry Queue Process-------------------"+Thread.currentThread().getName());
		logger.info("start Retry Queue Process------------------- "+Thread.currentThread().getName());
		List<Future<MSDNDetailsDTO>> futureList = new ArrayList<>();
		String keysTimeStamp = AppUtils.getCurrentTimeStamp();
		Future<MSDNDetailsDTO> result=null;
		for (int threadNumber = 0; threadNumber < appConstant.getRetryBatchSize(); threadNumber++) {
			List<MSDNDetailsDTO> listOfData = msisdnService.getRetryDetail(keysTimeStamp);
			if (listOfData != null) {
				for (MSDNDetailsDTO data : listOfData) {
					logger.info("Start Retry mobile no "+AppUtils.maskString(data.getMsisdn(), 0, "X") +" Retry Count : "+ data.getRetryCount() );
					Worker callableTask = new Worker(data, appConstant.getServiceUrl(), appConstant.getMerchentId(), appConstant.getMerchentKey(), appConstant.getMessage(),
							appConstant.getFirstRetryInterval(), appConstant.getSecondRetryInterval(), appConstant.getThiredRetryInterval(), msisdnService, pgpSecureUtils,
							smsPush, enterpriseService, appConstant.getSuccessResTemplate(), appConstant.getErrorResTemplate(), appConstant.getRetryEncounter());
					 result = threadPoolRetry.submit(callableTask);
					futureList.add(result);
				}

			} else {
				break;
			}
		}
		
		System.out.println("End Retry Process-------------------\n\n\n");
		logger.info("End Retry Process-------------------\n\n\n");
	}
}
