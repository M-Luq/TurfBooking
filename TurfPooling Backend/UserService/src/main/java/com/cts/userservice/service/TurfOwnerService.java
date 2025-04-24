package com.cts.userservice.service;

import java.util.List;

import com.cts.userservice.dto.TurfOwnerUpdateDTO;
import com.cts.userservice.entity.TurfOwner;

public interface TurfOwnerService {

	 TurfOwner saveTurfOwner(String userName,TurfOwner turfOwner);

	 TurfOwner getTurfOwnerById(Long id);

	 List<TurfOwner> getAllTurfOwners();
	 
//	 List<TurfDTO> getOwnedTurfs(Long ownerId);
	 
	 public List<Long> getTurfIdsByOwnerId(Long ownerId);

	 void deleteTurfOwner(Long id);
	
	 TurfOwner updateTurfOwner(Long id, TurfOwnerUpdateDTO ownerDetails);
    
	void updateOwnedTurfs(Long ownerId, String turfId) ;
	
	void removeOwnedTurf(Long ownerId, String turfId);
	
	boolean isRegistered(Long ownerId);
}
