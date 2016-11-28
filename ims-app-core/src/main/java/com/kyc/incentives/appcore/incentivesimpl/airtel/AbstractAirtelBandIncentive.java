/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.SingularAttribute;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.ImsRole;
import com.kyc.incentives.Incentive;
import com.kyc.incentives.IncentiveTriggerHistory;
import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.airtel.AirtelIncentivePayment;
import com.kyc.incentives.airtel.AirtelIncentivePayment_;
import com.kyc.incentives.appcore.contracts.pojo.IncentiveCalculatorContext;
import com.kyc.incentives.appcore.contracts.pojo.RegCountPojo;
import com.kyc.incentives.appcore.incentivesimpl.AbstractIncentiveCalculator;
import com.kyc.incentives.enums.UserTriggerHistoryStatus;

/**
 * @author dawuzi
 *
 */
public abstract class AbstractAirtelBandIncentive extends AbstractIncentiveCalculator { 
	
	private Map<String, AppUser> userNameMap = new HashMap<>();

	public AbstractAirtelBandIncentive() {
		super(AirtelService.getInstance());
	}

	@Override
	protected long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory) {
		return incentiveUserTriggerHistory.getUnitCount();
	}
	
	@Override
	protected void addHistory(Incentive incentive, AppUser user, IncentiveCalculatorContext context,
			List<IncentiveUserTriggerHistory> histories) {
		
		Date startDate = context.getIncentiveTriggerHistory().getStartDate();
		Date endDate = context.getIncentiveTriggerHistory().getEndDate();
		
		Calendar sept30 = Calendar.getInstance();
		Calendar aug31 = Calendar.getInstance();
		Calendar sept1 = Calendar.getInstance();
		Calendar aug1 = Calendar.getInstance();
		
		sept30.set(2016, 8, 30, 23, 59, 59);
		aug31.set(2016, 7, 31, 23, 59, 59);
		sept1.set(2016, 8, 1, 0, 0, 1);
		aug1.set(2016, 7, 1, 0, 0, 1);
		
		Set<ImsRole> roles = user.getRoles();
		
		boolean dealer = true;
		
		for(ImsRole role : roles){
			if("FSA".equals(role.getName())){
				dealer = false;
				break;
			}
		}
		
		List<String> months = new ArrayList<>();
		
		if(isOverlapping(startDate, endDate, sept1.getTime(), sept30.getTime())){
			months.add("September");
		}
		
		if(isOverlapping(startDate, endDate, aug1.getTime(), aug31.getTime())){
			months.add("August");
		}
		
		AirtelRegBand airtelBand = getAirtelBand();
		
		if(airtelBand == null){
			throw new IllegalStateException("getAirtelBand() returning null");
		}
		
		SingularAttribute<AirtelIncentivePayment, Long> bandMetaModelAttribute;
		
		switch (getAirtelBand()) {
		
		case BAND_100_199:
			bandMetaModelAttribute = AirtelIncentivePayment_.band100;
			break;
		case BAND_200_499:
			bandMetaModelAttribute = AirtelIncentivePayment_.band200;
			break;
		case BAND_500_999:
			bandMetaModelAttribute = AirtelIncentivePayment_.band500;
			break;
		case BAND_1000:
			bandMetaModelAttribute = AirtelIncentivePayment_.band1000;
			break;
		case POSTPAID_LINES:
			bandMetaModelAttribute = AirtelIncentivePayment_.postpaidLines;
			break;
		case VARIANCE:
			bandMetaModelAttribute = AirtelIncentivePayment_.variance;
			break;

		default:
			throw new IllegalStateException("Unknow band found");
		}
		
		String name = user.getName();
		
		List<RegCountPojo> countPojos = AirtelService.getInstance().getDealerAgentCounts(name, months, dealer, bandMetaModelAttribute);
		
		IncentiveTriggerHistory incentiveTriggerHistory = context.getIncentiveTriggerHistory();
		
		log.info("processing app user : "+name +", countPojos.size() : "+countPojos.size()+", getAirtelBand() : "+getAirtelBand());
		
		for(RegCountPojo countPojo : countPojos){
			
			IncentiveUserTriggerHistory history = new IncentiveUserTriggerHistory();
			
			history.setActive(true);
			history.setAmount(BigDecimal.ZERO);
			history.setCreateDate(context.getCreateTime());
			history.setEndDate(endDate);
			history.setId(null);
			history.setIncentive(incentive);
			history.setIncentiveTriggerHistory(incentiveTriggerHistory);
			history.setStartDate(startDate); 
			history.setStatus(UserTriggerHistoryStatus.PENDING);
			history.setTriggerEndTime(null);
			history.setTriggerStartTime(null);
			history.setUnitAmount(getUnitAmount(countPojo.getVendorType()));
			history.setUnitCount(countPojo.getCount());
			history.setRole(countPojo.getVendorType());
			
			if(dealer){
				history.setUser(getUser(countPojo.getAgent()));
				history.setUpLineUser(user);
			} else {
				history.setUser(user);
				history.setUpLineUser(getUser(countPojo.getDealer()));
			}
			
			histories.add(history);
		}
	}

	/**
	 * @param vendorType
	 * @param count 
	 * @return
	 */
	private double getUnitAmount(String vendorType) {
		return AirtelUtil.getUnitAmount(vendorType, getAirtelBand());
	}

	/**
	 * @param agent
	 * @return
	 */
	private AppUser getUser(String name) {
		
		AppUser appUser = userNameMap.get(name);
		
		if(appUser == null){
			appUser = AirtelService.getInstance().getUserByName(name); 
			userNameMap.put(name, appUser);
		}
		
		return appUser; 
	}

	public static boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
	    return start1.before(end2) && start2.before(end1);
	}
	
	public abstract AirtelRegBand getAirtelBand();
}
