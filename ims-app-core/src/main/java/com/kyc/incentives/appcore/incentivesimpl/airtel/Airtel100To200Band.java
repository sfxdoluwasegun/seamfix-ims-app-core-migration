/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.ImsRole;
import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.appcore.incentivesimpl.AbstractIncentiveCalculator;
import com.kyc.incentives.appcore.incentivesimpl.ActiveDevicesService;

/**
 * @author dawuzi
 *
 */
public class Airtel100To200Band extends AbstractIncentiveCalculator {

	public Airtel100To200Band() {
		super(AirtelService.getInstance());
	}

	@Override
	protected long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory) {
		
		Date startDate = incentiveUserTriggerHistory.getStartDate();
		Date endDate = incentiveUserTriggerHistory.getEndDate();
		
		Calendar sept30 = Calendar.getInstance();
		Calendar aug31 = Calendar.getInstance();
		Calendar sept1 = Calendar.getInstance();
		Calendar aug1 = Calendar.getInstance();
		
		sept30.set(2016, 8, 30, 23, 59, 59);
		aug31.set(2016, 7, 31, 23, 59, 59);
		sept1.set(2016, 8, 1, 0, 0, 1);
		aug1.set(2016, 7, 1, 0, 0, 1);
		
		List<String> months = new ArrayList<>();
		
		if(isOverlapping(startDate, endDate, sept1.getTime(), sept30.getTime())){
			months.add("September");
		}
		
		if(isOverlapping(startDate, endDate, aug1.getTime(), aug31.getTime())){
			months.add("August");
		}
		
		log.info("months : "+months+", startDate : "+startDate+", endDate : "+endDate);
		
		if(months.isEmpty()){
			return 0;
		}
		
		AppUser user = incentiveUserTriggerHistory.getUser();
		
		String name = user.getName();
		
		Set<ImsRole> roles = user.getRoles();
		
		boolean dealer = true;
		
		for(ImsRole role : roles){
			if("FSA".equals(role.getName())){
				dealer = false;
			}
		}
		
		Long count = AirtelService.getInstance().get100To200BandCount(name, months, dealer);
		
		if(count == null){
			return 0;
		}
		
		return count;
	}
	
	public static boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
	    return start1.before(end2) && start2.before(end1);
	}


}
