/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.enums.UserTriggerHistoryStatus;
import com.sf.biocapture.entity.enums.StatusType;

/**
 * @author dawuzi
 * @deprecated
 */
public class CleanRecordUtil {

	public static void handleTriggerHistories(Collection<IncentiveUserTriggerHistory> histories, List<StatusType> targetStatusTypes){
		
		Date triggerStartTime = new Date();
		
		for (IncentiveUserTriggerHistory incentiveUserTriggerHistory : histories) {
			
			try {
				
				incentiveUserTriggerHistory.setTriggerStartTime(triggerStartTime);
				incentiveUserTriggerHistory.setTriggerEndTime(new Date());
				
				double unitAmount = incentiveUserTriggerHistory.getUnitAmount();
				
				long count = getCount(incentiveUserTriggerHistory, targetStatusTypes);
				
				double totalAmount = count * unitAmount;
				
				BigDecimal amount = new BigDecimal(totalAmount);
				
				incentiveUserTriggerHistory.setAmount(amount); 
				incentiveUserTriggerHistory.setUnitCount(count);
				incentiveUserTriggerHistory.setStatus(UserTriggerHistoryStatus.SUCCESS);
				
			} catch (Exception e) {
				incentiveUserTriggerHistory.setStatus(UserTriggerHistoryStatus.FAILED);
			}
		}
		
		
	}

	/**
	 * @param incentiveUserTriggerHistory
	 * @return
	 */
	private static long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory, List<StatusType> targetStatusTypes) {
		Date startDate = incentiveUserTriggerHistory.getStartDate();
		Date endDate = incentiveUserTriggerHistory.getEndDate();
		
		AppUser user = incentiveUserTriggerHistory.getUser();
		
		String email = user.getEmail();
		
		Long cleanRecordCount = CleanRecordService.getInstance().getCleanRecordCount(email, startDate, endDate, targetStatusTypes);
		
		if(cleanRecordCount == null){
			return 0;
		}
		
		return cleanRecordCount;
	}
	
}
