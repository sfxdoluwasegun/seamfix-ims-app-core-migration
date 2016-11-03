/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.util.Date;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.IncentiveUserTriggerHistory;

/**
 * @author dawuzi
 *
 */
public class ActiveDevices extends AbstractIncentiveCalculator {

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

}
