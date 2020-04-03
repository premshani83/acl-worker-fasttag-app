package com.acl.pgp.security;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.acl.AppConstant;
import com.didisoft.pgp.PGPLib;

@Component
public class PGPSecurityUtils {
	private static final Logger logger=LoggerFactory.getLogger(PGPSecurityUtils.class);
	
	@Autowired
	private AppConstant appConstant;
	@Autowired
	ResourceLoader resourceLoader;
	
	private byte[] encrypte(byte[] data) {
		byte[] encryptedBytes=null;
		logger.info("come into encryption method");
		try {
			logger.info("file location"+appConstant.getPublicKeyFileLoc());
			Resource resource = resourceLoader.getResource("classpath:"+appConstant.getPublicKeyFileLoc());
			logger.info("public key file is loaded "+resource);
			 PGPLib pgpLib=new PGPLib();
			 logger.info("PGP lib is initiated: "+pgpLib);
			String encr= pgpLib.encryptString(new String(data), resource.getInputStream());
		
			encryptedBytes=encr.getBytes("UTF-8");
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("error comes into encrypt body", e);
		}
		return encryptedBytes;
	}
	
	public String base64Encode(String data) {
		String base64EncodedString=null;
		try {
			logger.info("come into convert into base 64");
			byte[]  encrypted=encrypte(data.getBytes("UTF-8"));
			base64EncodedString = new String(Base64.getEncoder().encode(encrypted));
		}catch (Exception e) {
			logger.error("exception comes into converted into base64 encoded", e);
			e.printStackTrace();
		}
		return base64EncodedString; 
	}
	
	private String decrypte(byte[] data) {
		 byte[] decryptedBytes=null;
		try {
			logger.info("privake key file location "+appConstant.getPrivateKeyFileLoc());
			Resource resource = resourceLoader.getResource("classpath:"+appConstant.getPrivateKeyFileLoc());
			PGPLib pgpLib=new PGPLib();
			String response=pgpLib.decryptString( new String(Base64.getDecoder().decode(data)), resource.getInputStream(), appConstant.getPrivateKeyPassword());
			decryptedBytes=response.getBytes("UTF-8");
		}catch (Exception e) {
			logger.error("error comes into decrypt data", e);
			e.printStackTrace();
		}
		return new String(decryptedBytes); 
	}
	
	public String base64Decode(String data) {
		String decrypted=decrypte(data.getBytes());
		return decrypted;
	}
}
