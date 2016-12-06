/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.util.Arrays;
import java.util.List;

import com.sf.biocapture.entity.enums.StatusType;

/**
 * @author dawuzi
 *
 */
public class DirtyRecord extends CleanRecord {
	
	private final List<StatusType> targetStatusTypes = Arrays.asList( new StatusType[]{ StatusType.FAILED } );

//	public DirtyRecord() {
//		super(CleanRecordService.getInstance());
//	}
//
//	@Override
//	protected long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory) {
//		Date startDate = incentiveUserTriggerHistory.getStartDate();
//		Date endDate = incentiveUserTriggerHistory.getEndDate();
//		
//		AppUser user = incentiveUserTriggerHistory.getUser();
//		
//		String email = user.getEmail();
//		
//		Long cleanRecordCount = CleanRecordService.getInstance().getCleanRecordDealerCount(email, startDate, endDate, targetStatusTypes);
//		
//		if(cleanRecordCount == null){
//			return 0;
//		}
//		
//		return cleanRecordCount;
//	}
	
	protected List<StatusType> getTargetStatusTypes(){
		return targetStatusTypes;
	}

	
}
