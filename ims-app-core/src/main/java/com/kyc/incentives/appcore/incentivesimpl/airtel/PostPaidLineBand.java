/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

/**
 * @author dawuzi
 *
 */
public class PostPaidLineBand extends AbstractAirtelBandIncentive {

	@Override
	public AirtelRegBand getAirtelBand() {
		return AirtelRegBand.POSTPAID_LINES;
	}

}
