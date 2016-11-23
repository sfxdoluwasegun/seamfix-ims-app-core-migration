/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

/**
 * @author dawuzi
 *
 */
public class Airtel500To1000Band extends AbstractAirtelBandIncentive {

	@Override
	public AirtelRegBand getAirtelBand() {
		return AirtelRegBand.BAND_500_999;
	}

}
