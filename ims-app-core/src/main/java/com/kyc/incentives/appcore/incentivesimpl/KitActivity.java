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
public class KitActivity extends AbstractIncentiveCalculator {

	public KitActivity() {
		super(KitActivityService.getInstance());
	}

	@Override
	protected long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory) {
		Date startDate = incentiveUserTriggerHistory.getStartDate();
		Date endDate = incentiveUserTriggerHistory.getEndDate();
		
		AppUser user = incentiveUserTriggerHistory.getUser();
		
		String email = user.getEmail();
		
		Long count = KitActivityService.getInstance().getActiveKitCount(email, startDate, endDate);
		
		if(count == null){
			return 0;
		}
		
		return count;
	}

}
