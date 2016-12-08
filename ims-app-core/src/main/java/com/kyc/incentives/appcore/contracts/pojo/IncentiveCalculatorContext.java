/**
 * 
 */
package com.kyc.incentives.appcore.contracts.pojo;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.Incentive;
import com.kyc.incentives.IncentiveTriggerHistory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author dawuzi
 *
 */
@Setter
@Getter
@ToString
public class IncentiveCalculatorContext {

	private Set<AppUser> users;
	private IncentiveTriggerHistory incentiveTriggerHistory;
	private AppUser triggerAgent;
	private List<Incentive> incentives;
	private Date createTime;
}
