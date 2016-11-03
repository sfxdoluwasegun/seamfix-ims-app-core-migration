/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.appcore.contracts.IncentiveCalculator;
import com.kyc.incentives.appcore.service.ImsService;
import com.kyc.incentives.enums.UserTriggerHistoryStatus;

/**
 * @author dawuzi
 *
 */
public abstract class AbstractIncentiveCalculator implements IncentiveCalculator {

	private ImsService imsService;
	protected Logger log = LoggerFactory.getLogger(getClass()); 
	private Logger localLog = LoggerFactory.getLogger(getClass()); 
	
	public AbstractIncentiveCalculator(ImsService imsService) {
		this.imsService = imsService;
	}

	@Override
	public Collection<IncentiveUserTriggerHistory> calculateIncentives(Collection<IncentiveUserTriggerHistory> histories) {
		Date triggerStartTime = new Date();
		
		for (IncentiveUserTriggerHistory incentiveUserTriggerHistory : histories) {
			
			try {
				
				incentiveUserTriggerHistory.setTriggerStartTime(triggerStartTime);
				incentiveUserTriggerHistory.setTriggerEndTime(new Date());
				
				double unitAmount = incentiveUserTriggerHistory.getUnitAmount();
				
				long count = getCount(incentiveUserTriggerHistory);
				
				double totalAmount = count * unitAmount;
				
				BigDecimal amount = new BigDecimal(totalAmount);
				
				incentiveUserTriggerHistory.setAmount(amount); 
				incentiveUserTriggerHistory.setUnitCount(count);
				incentiveUserTriggerHistory.setStatus(UserTriggerHistoryStatus.SUCCESS);
				
			} catch (Exception e) {
				incentiveUserTriggerHistory.setStatus(UserTriggerHistoryStatus.FAILED);
				localLog.error(null, e);
			}
		}
		
		imsService.updateBulk(histories);
		
		return histories;
	}

	/**
	 * @param incentiveUserTriggerHistory
	 * @return
	 */
	protected abstract long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory);

}
