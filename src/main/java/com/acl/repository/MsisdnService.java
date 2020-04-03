package com.acl.repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.acl.AppConstant;
import com.acl.model.MSDNDetailsDTO;

@Service
public class MsisdnService implements MsidnRepository {


	private RedisTemplate<String, MSDNDetailsDTO> redisTemplate;

	private ListOperations<String, MSDNDetailsDTO> listOps;
	
	private HashOperations<String, String, List<MSDNDetailsDTO>> hashOps;
	@Autowired
	private AppConstant appConstant;
	@Autowired
	public MsisdnService(RedisTemplate<String, MSDNDetailsDTO> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@PostConstruct
	private void intializeListOperations() {
		listOps = redisTemplate.opsForList();
		hashOps = redisTemplate.opsForHash();
	}

	
	@Override
	public void saveProcessed(MSDNDetailsDTO msdisdnDetails) {
		listOps.rightPush("PROCESSED", msdisdnDetails);
	}
	@Override
	public void saveUnProcessed(MSDNDetailsDTO msdisdnDetails) {
		listOps.rightPush("UNPROCESSED", msdisdnDetails);
	}

	@Override
	public MSDNDetailsDTO leftPop(String keys) {
		return listOps.leftPop(keys);
	}
	
	@Override
	public void saveRetry(String keyAsTimeStamp, MSDNDetailsDTO msdnDetailsDTO) {
		List<MSDNDetailsDTO> listOfRetry= hashOps.get(appConstant.getRetryQueue(), keyAsTimeStamp);
		if(listOfRetry==null) {
			listOfRetry=new ArrayList<MSDNDetailsDTO>();
		}
		listOfRetry.add(msdnDetailsDTO);
		hashOps.put(appConstant.getRetryQueue(), keyAsTimeStamp, listOfRetry);
	}
	
	@Override
	public List<MSDNDetailsDTO> getRetryDetail(String keyAsTimeStamp) {
		List<MSDNDetailsDTO> list=hashOps.get(appConstant.getRetryQueue(), keyAsTimeStamp);
		if(list!=null) {
			hashOps.delete(appConstant.getRetryQueue(), keyAsTimeStamp);
		}
		return list;
		
	}
	
}