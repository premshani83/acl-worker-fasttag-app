package com.acl.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.acl.AppConstant;
import com.acl.AppUtils;
import com.acl.enterprise.service.EnterpriseService;
import com.acl.enterprise.service.SMSPush;
import com.acl.model.MSDNDetailsDTO;
import com.acl.pgp.security.PGPSecurityUtils;
import com.acl.repository.MsisdnService;

@Service
public class NewQueueExecutor {

	private final static Logger logger = LoggerFactory.getLogger(NewQueueExecutor.class);
	

	@Autowired
	private AppConstant appConstant;
	@Autowired
	private ThreadPoolTaskExecutor threadPoolNew;

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
    private Environment environment;
	
	@Async
	@Scheduled(fixedRate = 1000)
	public void startProcessor() throws Exception {
		System.out.println("start New Queue Process-------------------");
		
		System.out.println("propeties change value batchNewSize "+ appConstant.getBatchNewSize());
		System.out.println("current Thread is working  "+Thread.currentThread().getName());
		logger.info("start New Queue Process-------------------");
		List<Future<MSDNDetailsDTO>> futureList = new ArrayList<>();
		for (int threadNumber = 0; threadNumber < appConstant.getBatchNewSize(); threadNumber++) {
			MSDNDetailsDTO data = msisdnService.leftPop(appConstant.getNewQueue());
			
			if(data ==null || data.getMsisdn()==null || data.getMsisdn().length()!=10 ) {
				if(data!=null)
					logger.info("Invalid mobile no, will not process! "+AppUtils.maskString(data.getMsisdn(), 0, "X") );
				continue;
			}
			if (data != null) {
				logger.info("try to process -------------------"+ AppUtils.maskString(data.getMsisdn(), 0, "X"));
				Worker callableTask = new Worker(data, appConstant.getServiceUrl(), appConstant.getMerchentId(), appConstant.getMerchentKey(), appConstant.getMessage(), appConstant.getFirstRetryInterval(),
						appConstant.getSecondRetryInterval(), appConstant.getThiredRetryInterval(), msisdnService, pgpSecureUtils, smsPush,
						enterpriseService, appConstant.getSuccessResTemplate(), appConstant.getErrorResTemplate(), appConstant.getRetryEncounter());
				Future<MSDNDetailsDTO> result = threadPoolNew.submit(callableTask);
				futureList.add(result);
			} 
		}
		
		System.out.println("End New Process-------------------\n\n\n");
		logger.info("End New Process-------------------\n\n\n");
	}

	
	@Bean
	@ConditionalOnProperty(name = "spring.config.location", matchIfMissing = false)
	public PropertiesConfiguration propertiesConfiguration(
	  @Value("${spring.config.location}") String path) throws Exception {
		System.out.println("Reload Properties load from ............"+path);
	    String filePath = new File(path.substring("file:".length())).getCanonicalPath();
	    PropertiesConfiguration configuration = new PropertiesConfiguration(
	      new File(filePath));
	    configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
	    return configuration;
	}
}
