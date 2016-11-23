/**
 * 
 */
package com.kyc.incentives.appcore.contracts;

import java.util.Collection;

import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.appcore.contracts.pojo.IncentiveCalculatorContext;

/**
 * @author dawuzi
 *
 */
public interface IncentiveCalculator {
//	Collection<IncentiveUserTriggerHistory> calculateIncentives(Collection<IncentiveUserTriggerHistory> incentiveUserTriggerHistories);
	Collection<IncentiveUserTriggerHistory> getUserTriggerHistories(IncentiveCalculatorContext context);
}
