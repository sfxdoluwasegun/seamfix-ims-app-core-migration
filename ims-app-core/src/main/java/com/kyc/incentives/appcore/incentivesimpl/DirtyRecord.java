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
	
	protected List<StatusType> getTargetStatusTypes(){
		return targetStatusTypes;
	}
}
