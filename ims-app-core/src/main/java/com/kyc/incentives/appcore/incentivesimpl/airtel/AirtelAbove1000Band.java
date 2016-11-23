/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

/**
 * @author dawuzi
 *
 */
public class AirtelAbove1000Band extends AbstractAirtelBandIncentive {

	@Override
	public AirtelRegBand getAirtelBand() {
		return AirtelRegBand.BAND_1000;
	}

}
