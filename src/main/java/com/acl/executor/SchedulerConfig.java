package com.acl.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@EnableScheduling
@Configuration
@PropertySource(value = "file:./aclfasttag.properties")
public class SchedulerConfig {

	@Value("${threadpool.corepoolsize}")
    int corePoolSize;
     
    @Value("${threadpool.maxpoolsize}")
    int maxPoolSize;
     
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setWaitForTasksToCompleteOnShutdown(true);
       
        return pool;
    }
}
