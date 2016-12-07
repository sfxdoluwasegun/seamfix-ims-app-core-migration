/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.ImsRole;
import com.kyc.incentives.Incentive;
import com.kyc.incentives.IncentiveTriggerHistory;
import com.kyc.incentives.IncentiveUserTriggerHistory;
import com.kyc.incentives.appcore.contracts.pojo.IncentiveCalculatorContext;
import com.kyc.incentives.enums.UserTriggerHistoryStatus;
import com.sf.biocapture.entity.DealerType;
import com.sf.biocapture.entity.KycDealer;
import com.sf.biocapture.entity.enums.StatusType;
import com.sf.biocapture.entity.security.KMRole;
import com.sf.biocapture.entity.security.KMUser;

/**
 * @author dawuzi
 *
 */
public class CleanRecord extends AbstractIncentiveCalculator {
	
	private CleanRecordService service = CleanRecordService.getInstance();
	private SharedService sharedService = SharedService.getInstance();
	
	private Map<String, AppUser> emailUserCache = new HashMap<>();
	private Map<String, KycDealer> agentDealerEmailCache = new HashMap<>();
	private Map<String, ImsRole> roleCache = new HashMap<>();
	private Map<DealerType, ImsRole> dealerTypeCache = new HashMap<>();
	
	public CleanRecord() {
		super(CleanRecordService.getInstance());
	}

	private final List<StatusType> targetStatusTypes = Arrays.asList( new StatusType[]{ StatusType.PASSED } );
	
	@Override
	protected long getCount(IncentiveUserTriggerHistory incentiveUserTriggerHistory) {
		return incentiveUserTriggerHistory.getUnitCount();
	}

	@Override
	protected void addHistory(Incentive incentive, AppUser appUser, IncentiveCalculatorContext context,
			List<IncentiveUserTriggerHistory> histories) {
		
		Date startDate = context.getIncentiveTriggerHistory().getStartDate();
		Date endDate = context.getIncentiveTriggerHistory().getEndDate();
		IncentiveTriggerHistory incentiveTriggerHistory = context.getIncentiveTriggerHistory();
		
		String email = appUser.getEmail();
		
		if(email == null || email.isEmpty()){
			return;
		}
		
		if(appUser.isBottom()){
			
			KycDealer dealer = getKycDealerByAgentEmail(email);
			
			AppUser uplineUser = getAppUserbyKycDealer(dealer);
			
			Long cleanRecordCount = service.getCleanRecordAgentCount(email, startDate, endDate, getTargetStatusTypes());
			
			if(cleanRecordCount == null){
				cleanRecordCount = 0L;
			}
			
			IncentiveUserTriggerHistory history = getHistory(context, startDate, endDate, incentive, 
					incentiveTriggerHistory, cleanRecordCount, appUser, uplineUser);
			
			histories.add(history);
			
		} else {
			
			List<KMUser> agents = service.getAgents(email);
			
			if(agents == null || agents.isEmpty()){
				return;
			}
			
			for(KMUser agent : agents){
				
				String agentEmail = agent.getEmailAddress();
				
				Long cleanRecordCount = service.getCleanRecordAgentCount(agentEmail, startDate, endDate, getTargetStatusTypes());
				
				if(cleanRecordCount == null){
					cleanRecordCount = 0L;
				}
				
				AppUser agentUser = getAgentUserByKmUser(agent, appUser);
				
				IncentiveUserTriggerHistory history = getHistory(context, startDate, endDate, incentive, 
						incentiveTriggerHistory, cleanRecordCount, agentUser, appUser);
				
				histories.add(history);
			}
		}
	}
	
	protected List<StatusType> getTargetStatusTypes(){
		return targetStatusTypes;
	}

	/**
	 * @param dealer
	 * @return
	 */
	private AppUser getAppUserbyKycDealer(KycDealer dealer) {
		
		if(dealer == null){
			return null;
		}
		
		String emailAddress = dealer.getEmailAddress();
		
		AppUser user = getUserByEmail(emailAddress, dealer.getPk());
		
		if(user != null){
			return user;
		}
		
		user = new AppUser();
		
		user.setBottom(false);
		user.setCreateDate(new Date());
		user.setDirectUpLineUser(null);
		user.setEmail(emailAddress);
		user.setName(dealer.getName());
		user.setOrbitaId(dealer.getOrbitaId());
		user.setReferencedId(dealer.getPk());
		user.setRoles(getRoles(dealer.getDealerType())); 
		user.setTopMostUpLineUser(null);
		
		service.create(user);
		
		emailUserCache.put(emailAddress, user);
		
		return user;
	}

	/**
	 * @param dealerType
	 * @return
	 */
	private Set<ImsRole> getRoles(DealerType dealerType) {
		
		Set<ImsRole> localRoles = new HashSet<>();
		
		ImsRole imsRole = dealerTypeCache.get(dealerType);
		
		if(imsRole == null){
			
			String code = "DEALER_TYPE";
			
			if(dealerType.getCode() == null){
				imsRole = sharedService.getRoleByName(dealerType.getName());
			} else {
				imsRole = sharedService.getRoleByNameAndCode(dealerType.getName(), code);
			}
			
			if(imsRole == null){
				
				imsRole = new ImsRole();
				
				imsRole.setCode(code);
				imsRole.setCreateDate(new Date());
				imsRole.setName(dealerType.getName());
				imsRole.setReferencedId(dealerType.getPk());

				service.create(imsRole);
			}
			
			dealerTypeCache.put(dealerType, imsRole);
		}
		
		localRoles.add(imsRole);
		
		return localRoles;
	}

	/**
	 * @param agent
	 * @param upLineUser 
	 * @return
	 */
	private AppUser getAgentUserByKmUser(KMUser agent, AppUser upLineUser) {
		
		String emailAddress = agent.getEmailAddress();
		
		AppUser user = getUserByEmail(emailAddress, agent.getPk());
		
		if(user != null){
			return user;
		}
		
		Set<AppUser> upLineUsers = null;
		
		if(upLineUser != null){
			upLineUsers = new HashSet<>();
			upLineUsers.add(upLineUser);
		}
		
		user = new AppUser();
		
		user.setBottom(true);
		user.setCreateDate(new Date());
		user.setDirectUpLineUser(upLineUser);
		user.setEmail(emailAddress);
		user.setName(getName(agent));
		user.setOrbitaId(agent.getOrbitaId());
		user.setReferencedId(agent.getPk());
		user.setRoles(getRoles(agent.getRoles())); 
		user.setUpLineUsers(upLineUsers);
		user.setTopMostUpLineUser(upLineUser);
		
		service.create(user);
		
		return user;
	}

	/**
	 * @param roles
	 * @return
	 */
	private Set<ImsRole> getRoles(Set<KMRole> roles) {
		
		if(roles == null || roles.isEmpty()){
			return null;
		}
		
		Set<ImsRole> localRoles = new HashSet<>();
		
		for(KMRole role : roles){
			localRoles.add(getRole(role));
		}
		
		return localRoles;
	}

	/**
	 * @param role
	 * @return
	 */
	private ImsRole getRole(KMRole kmRole) {
		
		String role = kmRole.getRole();
		
		ImsRole imsRole = roleCache.get(role);
		
		if(imsRole != null){
			return imsRole;
		}
		
		imsRole = sharedService.getRoleByName(role);
		
		if(imsRole == null){
			
			imsRole = new ImsRole();
			
			imsRole.setCode("KM_ROLE");
			imsRole.setCreateDate(new Date());
			imsRole.setName(role);
			imsRole.setReferencedId(kmRole.getPk());

			service.create(imsRole);
		}
		
		roleCache.put(role, imsRole);
		
		return imsRole;
	}

	/**
	 * @param agent
	 * @return
	 */
	private String getName(KMUser agent) {
		
		String name = (agent.getSurname() != null ? agent.getSurname() : "")
				+ (agent.getFirstName() != null ? " " + agent.getFirstName() : "")
				+ (agent.getOtherName() != null ? " " + agent.getOtherName() : "")
				;
		return name.trim();
	}

	/**
	 * @return
	 */
	private IncentiveUserTriggerHistory getHistory(IncentiveCalculatorContext context, Date startDate, Date endDate, Incentive incentive, 
			IncentiveTriggerHistory incentiveTriggerHistory, long unitCount, AppUser user, AppUser uplineUser) {
		
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
		history.setUnitAmount(incentive.getAmount());
		history.setUnitCount(unitCount);
		history.setRole(getRole(user));
		history.setUser(user);
		history.setUpLineUser(uplineUser);
		
		return history;
	}

	/**
	 * @param dealerEmail
	 * @return
	 */
	private AppUser getUserByEmail(String email, Long referenceId) {
		
		String key = email + referenceId;
		
		AppUser user = emailUserCache.get(key);
		
		if(user == null){
			user = service.getUserByEmailAndRefId(email, referenceId);
			if(user !=  null){
				emailUserCache.put(key, user);
			}
		}
		
		return user;
	}

	/**
	 * @param agentEmail
	 * @return
	 */
	private KycDealer getKycDealerByAgentEmail(String agentEmail) {
		
		KycDealer dealer = agentDealerEmailCache.get(agentEmail);
		
		if(dealer == null){
			dealer = service.getKycDealerByAgentEmail(agentEmail);
			if(dealer != null){
				agentDealerEmailCache.put(agentEmail, dealer);
			}
		}
		
		return dealer;
	}

	@Override
	protected List<AppUser> getTargetUsers() {
		
		List<KMUser> allKMusers = service.getAllKMUsers();
		
		if(allKMusers == null || allKMusers.isEmpty()){
			return super.getTargetUsers();
		}
		
		List<AppUser> users = new ArrayList<>();
		
		
		for(KMUser user : allKMusers){
			
			String emailAddress = user.getEmailAddress();
			
			KycDealer dealer = getKycDealerByAgentEmail(emailAddress);
			
			AppUser dealerAppUser = getAppUserbyKycDealer(dealer);
			
			AppUser anAgentUser = getAgentUserByKmUser(user, dealerAppUser);
			
			users.add(anAgentUser);
		}
		
		return users;
	}
	
}
