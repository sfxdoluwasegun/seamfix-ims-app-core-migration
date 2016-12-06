/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.ImsRole;
import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.sf.biocapture.entity.DealerType;
import com.sf.biocapture.entity.KycDealer;

/**
 * @author dawuzi
 *
 */
public class ActiveDevices extends AbstractIncentiveCalculator {

	private ActiveDevicesService service = ActiveDevicesService.getInstance();
	private SharedService sharedService = SharedService.getInstance();
	private Map<DealerType, ImsRole> dealerTypeCache = new HashMap<>();
	
	/**
	 * @param imsService
	 */
	public ActiveDevices() {
		super(ActiveDevicesService.getInstance());
	}

	@Override
	protected long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory) {
		Date startDate = incentiveUserTriggerHistory.getStartDate();
		Date endDate = incentiveUserTriggerHistory.getEndDate();
		
		AppUser user = incentiveUserTriggerHistory.getUser();
		
		String email = user.getEmail();
		
		Long count = ActiveDevicesService.getInstance().getActiveDevicesCount(email, startDate, endDate);
		
		if(count == null){
			return 0;
		}
		
		return count;
	}

	@Override
	protected List<AppUser> getTargetUsers() {
		List<KycDealer> allTargetDealers = sharedService.getAllNodeAssignmentDealers();
		
		if(allTargetDealers == null){
			return Collections.emptyList();
		}
		
		List<AppUser> users = new ArrayList<>();
		
		for (KycDealer kycDealer : allTargetDealers) {
			AppUser user = getAppUserByKycDealer(kycDealer);
			
			if(user != null){
				users.add(user);
			}
		}
		
		return users;
	}

	/**
	 * @param kycDealer
	 * @return
	 */
	private AppUser getAppUserByKycDealer(KycDealer dealer) {

		if(dealer == null){
			return null;
		}
		
		String emailAddress = dealer.getEmailAddress();
		
		AppUser user = getUserByEmail(emailAddress, dealer.getPk());
		
		if(user != null){
			return user;
		}
		
		user = new AppUser();
		
		user.setBottom(false);
		user.setCreateDate(new Date());
		user.setDirectUpLineUser(null);
		user.setEmail(emailAddress);
		user.setName(dealer.getName());
		user.setOrbitaId(dealer.getOrbitaId());
		user.setReferencedId(dealer.getPk());
		user.setRoles(getRoles(dealer.getDealerType())); 
		user.setTopMostUpLineUser(null);
		
		service.create(user);
		
		return user;
	}

	/**
	 * @param emailAddress
	 * @param pk
	 * @return
	 */
	private AppUser getUserByEmail(String email, Long referenceId) {
		AppUser user = service.getUserByEmailAndRefId(email, referenceId);
		return user;
	}
	
	/**
	 * @param dealerType
	 * @return
	 */
	private Set<ImsRole> getRoles(DealerType dealerType) {
		
		Set<ImsRole> localRoles = new HashSet<>();
		
		ImsRole imsRole = dealerTypeCache.get(dealerType);
		
		if(imsRole == null){
			
			String code = "DEALER_TYPE";
			
			if(dealerType.getCode() == null){
				imsRole = sharedService.getRoleByName(dealerType.getName());
			} else {
				imsRole = sharedService.getRoleByNameAndCode(dealerType.getName(), code);
			}
			
			if(imsRole == null){
				
				imsRole = new ImsRole();
				
				imsRole.setCode(code);
				imsRole.setCreateDate(new Date());
				imsRole.setName(dealerType.getName());
				imsRole.setReferencedId(dealerType.getPk());

				service.create(imsRole);
			}
			
			dealerTypeCache.put(dealerType, imsRole);
		}
		
		localRoles.add(imsRole);
		
		return localRoles;
	}
	
}
