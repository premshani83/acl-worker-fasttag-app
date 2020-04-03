package com.acl.repository;

import java.util.List;

import com.acl.model.MSDNDetailsDTO;

public interface MsidnRepository {

	void saveProcessed(MSDNDetailsDTO msddetailsDto);
	
	void saveUnProcessed(MSDNDetailsDTO msddetailsDto);
	MSDNDetailsDTO leftPop(String msisdn);
	void saveRetry(String KeyAsTimeStamp, MSDNDetailsDTO msdnDetailsDTO);
	List<MSDNDetailsDTO> getRetryDetail(String keyAsTimeStamp);
}