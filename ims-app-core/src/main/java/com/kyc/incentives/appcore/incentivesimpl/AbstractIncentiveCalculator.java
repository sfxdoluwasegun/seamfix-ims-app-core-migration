/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.Incentive;
import com.kyc.incentives.IncentiveTriggerHistory;
import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.appcore.contracts.IncentiveCalculator;
import com.kyc.incentives.appcore.contracts.pojo.IncentiveCalculatorContext;
import com.kyc.incentives.appcore.service.ImsService;
import com.kyc.incentives.enums.UserTriggerHistoryStatus;

/**
 * @author dawuzi
 *
 */
public abstract class AbstractIncentiveCalculator implements IncentiveCalculator {

	private ImsService imsService;
	protected Logger log = LoggerFactory.getLogger(getClass()); 
	private Logger localLog = LoggerFactory.getLogger(AbstractIncentiveCalculator.class); 
	
	public AbstractIncentiveCalculator(ImsService imsService) {
		this.imsService = imsService;
	}

	protected Collection<IncentiveUserTriggerHistory> calculateIncentives(Collection<IncentiveUserTriggerHistory> histories) {
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
		
		imsService.createOrUpdateBulk(histories);
		
		return histories;
	}

	protected List<IncentiveUserTriggerHistory> getTriggerHistories(IncentiveCalculatorContext context){
		
		List<AppUser> users = context.getUsers();
		
		List<IncentiveUserTriggerHistory> histories = new ArrayList<>();
		
		for (AppUser appUser : users) {
			addUserTriggerHistories(appUser, context, histories);
		}
		
		return histories;
	}
	
	/**
	 * @param appUser
	 * @param context
	 * @return
	 */
	protected void addUserTriggerHistories(AppUser appUser, IncentiveCalculatorContext context, List<IncentiveUserTriggerHistory> histories) {

		List<Incentive> incentives = context.getIncentives();
		
		for(Incentive incentive : incentives){
			
			if(!Collections.disjoint(incentive.getTargetRoles(), appUser.getRoles())){
				addHistory(incentive, appUser, context, histories);
			}
			
		}
		
	}

	/**
	 * @param incentive
	 * @param appUser
	 * @param context
	 * @return
	 */
	protected void addHistory(Incentive incentive, AppUser appUser, IncentiveCalculatorContext context, 
			List<IncentiveUserTriggerHistory> histories) {
		
		IncentiveTriggerHistory incentiveTriggerHistory = context.getIncentiveTriggerHistory();
		
		Date startDate = incentiveTriggerHistory.getStartDate();
		Date endDate = incentiveTriggerHistory.getEndDate();
		
		IncentiveUserTriggerHistory history = new IncentiveUserTriggerHistory();
		
		history.setActive(true);
		history.setAmount(BigDecimal.ZERO);
		history.setCreateDate(context.getCreateTime());
		history.setEndDate(endDate);
		history.setId(null);
		history.setIncentive(incentive);
		history.setIncentiveTriggerHistory(incentiveTriggerHistory);
		history.setStartDate(startDate); 
		history.setStatus(UserTriggerHistoryStatus.PENDING);
		history.setTriggerEndTime(null);
		history.setTriggerStartTime(null);
		history.setUnitAmount(incentive.getAmount());
		history.setUnitCount(0);
		history.setUser(appUser);
		
		histories.add(history);
		
	}

	@Override
	public Collection<IncentiveUserTriggerHistory> getUserTriggerHistories(IncentiveCalculatorContext context) {
		
		List<IncentiveUserTriggerHistory> triggerHistories = getTriggerHistories(context);
		
		return calculateIncentives(triggerHistories);
	}

	/**
	 * @param incentiveUserTriggerHistory
	 * @return
	 */
	protected abstract long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory);

}
