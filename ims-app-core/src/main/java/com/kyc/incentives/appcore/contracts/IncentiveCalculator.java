/**
 * 
 */
package com.kyc.incentives.appcore.contracts;

import java.util.Collection;

import com.kyc.incentives.IncentiveUserTriggerHistory;

/**
 * @author dawuzi
 *
 */
public interface IncentiveCalculator {
	Collection<IncentiveUserTriggerHistory> calculateIncentives(Collection<IncentiveUserTriggerHistory> incentiveUserTriggerHistories);
}
