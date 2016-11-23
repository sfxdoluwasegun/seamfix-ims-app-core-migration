/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

/**
 * @author dawuzi
 *
 */
public class Airtel200To500Band extends AbstractAirtelBandIncentive {

	@Override
	public AirtelRegBand getAirtelBand() {
		return AirtelRegBand.BAND_200_499;
	}

}
