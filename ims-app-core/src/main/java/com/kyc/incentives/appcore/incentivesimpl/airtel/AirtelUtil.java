/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

/**
 * @author dawuzi
 *
 */
public class AirtelUtil {

	/**
	 * @param vendorType
	 * @param band100199
	 * @return
	 */
	public static double getUnitAmount(String vendorType, AirtelRegBand band) {
	
		if(vendorType == null){
			return 0D;
		}
		
		switch (vendorType) {
		
		case "3RD PARTY VENDOR":
			return 120D;
		case "SIM SELLING CANVASSING AGENCY":
			return 100D;
		case "CHANNEL PARTNER":
			return 100D;
		case "DOOR2DOOR":
			return getDoorToDoor(band);
		case "CORPORATE TEAM":
//			return getCorporateTeam(band);
			return 80D;
		case "HVI":
			return 80D;
//			return getHvi(band);
		case "AIRTEL EXPRESS SHOP":
			return 100D;
		case "ASM ACTIVATION":
			return getAsmActivation(band);
		case "SIM DISTRIBUTOR":
			return 100D;
		case "FREELANCER":
			return 120D;
		case "DIRECT SALES AGENT":
			return 80D;
		case "CP OWNED KIT":
			return 120D;
		case "":
		case "N/A":
		case "SHOWROOM":
		case "IT WAREHOUSE":
		case "INTERNAL USE":
			return 0D;
			

		default:
			break;
		}
		
		return 0;
		
	}

	/**
	 * @param band
	 * @return
	 */
	private static double getAsmActivation(AirtelRegBand band) {
		switch (band) {
		
		case BAND_100_199:
			return 60D;

		case BAND_200_499:
		case BAND_500_999:
			return 80D;

		case BAND_1000:
			return 100D;
			
		case POSTPAID_LINES:
			return 60D;
			
		default:
			return 0D;
		}
	}

//	/**
//	 * @param band
//	 * @return
//	 */
//	private static double getHvi(AirtelRegBand band) {
//		switch (band) {
//		
//		case BAND_100_199:
//			return 60D;
//
//		case BAND_200_499:
//		case BAND_500_999:
//			return 80D;
//
//		case BAND_1000:
//			return 100D;
//			
//		default:
//			return 0D;
//		}
//	}

	/**
	 * @param band
	 * @return
	 */
	private static double getCorporateTeam(AirtelRegBand band) {
		switch (band) {
		
		case BAND_100_199:
			return 50D;

		case BAND_200_499:
		case BAND_500_999:
			return 80D;

		case BAND_1000:
			return 100D;
			
		default:
			return 0D;
		}
	}

	/**
	 * @param band
	 * @return
	 */
	private static double getDoorToDoor(AirtelRegBand band) {
		switch (band) {
		
		case BAND_100_199:
			return 60D;

		case BAND_200_499:
		case BAND_500_999:
			return 80D;

		case BAND_1000:
			return 100D;
			
		case POSTPAID_LINES:
			return 60D;
			
		default:
			return 0D;
		}
	}
}
