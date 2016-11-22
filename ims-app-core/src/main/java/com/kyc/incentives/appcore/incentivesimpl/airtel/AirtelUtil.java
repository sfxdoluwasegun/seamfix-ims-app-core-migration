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
	 * @param count 
	 * @param band100199
	 * @return
	 */
	public static double getUnitAmount(String vendorType, AirtelRegBand band, Long count) {
	
		if(vendorType == null){
			return 0D;
		}
		
		switch (vendorType) {
		
		case "3RD PARTY VENDOR":
			return 120D;
		case "SIM SELLING CANVASSING AGENCY":
			return 100D;
		case "CHANNEL PARTNER":
//			return getChannelPartner(band, count);
			return 100D;
		case "DOOR2DOOR":
			return getDoorToDoor(band);
		case "CORPORATE TEAM":
//			return getCorporateTeam(band, count);
			return 80D;
		case "HVI":
			return 80D;
//			return getHvi(band);
		case "AIRTEL EXPRESS SHOP":
//			return getAirtelExpressShop(band, count);
			return 100D;
		case "ASM ACTIVATION":
			return getAsmActivation(band, count);
		case "SIM DISTRIBUTOR":
			return 100D;
		case "FREELANCER":
			return getFreelancer(band);
		case "DIRECT SALES AGENT":
			return 80D;
		case "CP OWNED KIT":
//			return getCpOwnedKit(band, count);
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
	 * @param count
	 * @return
	 */
	private static double getCpOwnedKit(AirtelRegBand band, Long count) {
		switch (band) {
		
		case VARIANCE:
			if(count < 0){
				return 0D; // variance was ignored for asm activation in the excel
			} else {
				return 60D;
			}
			
		default:
			return 120D;
		}
	}

	/**
	 * @param band
	 * @param count
	 * @return
	 */
	private static double getCorporateTeam(AirtelRegBand band, Long count) {
		switch (band) {
		
		case VARIANCE:
			if(count < 0){
				return 0D; // variance was ignored for asm activation in the excel
			} else {
				return 60D;
			}
			
		default:
			return 80D;
		}
	}

	/**
	 * @param band
	 * @param count
	 * @return
	 */
	private static double getChannelPartner(AirtelRegBand band, Long count) {
		switch (band) {
		
		case VARIANCE:
			if(count < 0){
				return 0D; // variance was ignored for asm activation in the excel
			} else {
				return 60D;
			}
			
		default:
			return 100D;
		}
	}

	/**
	 * @param band
	 * @param count
	 * @return
	 */
	private static double getAirtelExpressShop(AirtelRegBand band, Long count) {
		switch (band) {
		
		case VARIANCE:
			if(count < 0){
				return 0D; // variance was ignored for asm activation in the excel
			} else {
				return 60D;
			}
			
		default:
			return 100D;
		}
	}

	/**
	 * @param band
	 * @return
	 */
	private static double getFreelancer(AirtelRegBand band) {
		switch (band) {
		
		case VARIANCE:
			return 0D; // variance was ignored for freelancers in the excel
			
		default:
			return 120D;
		}
	}

	/**
	 * @param band
	 * @param count 
	 * @return
	 */
	private static double getAsmActivation(AirtelRegBand band, Long count) {
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
			
		case VARIANCE:
			if(count < 0){
				return 0D; // variance was ignored for asm activation in the excel
			} else {
				return 60D;
			}
			
		default:
			return 0D;
		}
	}

	/**
	 * @param band
	 * @param count 
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
			
		case VARIANCE:
			return 0D; // variance was ignored for door to door in the excel
			
		default:
			return 0D;
		}
	}
}
