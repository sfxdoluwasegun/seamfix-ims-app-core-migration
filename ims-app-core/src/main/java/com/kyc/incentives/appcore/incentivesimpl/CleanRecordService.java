/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import com.kyc.incentives.appcore.service.ImsService;

/**
 * @author dawuzi
 *
 */
public class CleanRecordService extends ImsService{

	/**
	 * static Singleton instance
	 */
	private static CleanRecordService instance;

	/**
	 * Private constructor for singleton
	 */
	private CleanRecordService() {
	}

	/**
	 * Static getter method for retrieving the singleton instance
	 */
	public static CleanRecordService getInstance() {
		if (instance == null) {
			instance = new CleanRecordService();
		}
		return instance;
	}
	
	
}
