/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.appcore.contracts.IncentiveCalculator;
import com.sf.biocapture.entity.enums.StatusType;

/**
 * @author dawuzi
 *
 */
public class CleanRecord implements IncentiveCalculator {
	
	private final List<StatusType> targetStatusTypes = Arrays.asList( new StatusType[]{ StatusType.PASSED } );
	
	@Override
	public Collection<IncentiveUserTriggerHistory> calculateIncentives(Collection<IncentiveUserTriggerHistory> histories) {
		CleanRecordUtil.handleTriggerHistories(histories, targetStatusTypes);
		CleanRecordService.getInstance().updateBulk(histories);
		return histories;
	}

}
