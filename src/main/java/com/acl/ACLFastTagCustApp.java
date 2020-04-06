package com.acl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;


/**
 * Its main class wich able to start the application
 * Its class enable to spring boot, redish cache and retry machnaism
 * @author prem.sahani
 *
 */
@SpringBootApplication
@EnableCaching
@EnableRetry
public class ACLFastTagCustApp {

	/**
	 * Its main method which use to call by Startup
	 * @param args no need to any argumnets
	 */
	public static void main(String[] args) {
		SpringApplication.run(ACLFastTagCustApp.class, args);
	}
	
	
}
