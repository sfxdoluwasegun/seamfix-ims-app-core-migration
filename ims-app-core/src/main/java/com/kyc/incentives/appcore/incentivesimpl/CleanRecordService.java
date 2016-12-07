/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.kyc.incentives.appcore.service.ImsService;
import com.sf.biocapture.entity.BasicData;
import com.sf.biocapture.entity.KycDealer;
import com.sf.biocapture.entity.SmsActivationRequest;
import com.sf.biocapture.entity.enums.StatusType;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.entity.validation.ValidationResult;
import com.sf.biocapture.entity.validation.ValidationResult_;

/**
 * @author dawuzi
 *
 */
public class CleanRecordService extends ImsService{

	/**
	 * static Singleton instance
	 */
	private static CleanRecordService instance;

	/**
	 * Private constructor for singleton
	 */
	private CleanRecordService() {
	}

	/**
	 * Static getter method for retrieving the singleton instance
	 */
	public static CleanRecordService getInstance() {
		if (instance == null) {
			instance = new CleanRecordService();
		}
		return instance;
	}

	/**
	 * 
	 * @param dealerEmail
	 * @param startDate
	 * @param endDate
	 * @param targetStatusTypes
	 * @return
	 */
	public Long getCleanRecordDealerCount(String dealerEmail, Date startDate, Date endDate, List<StatusType> targetStatusTypes) {
		return getCleanRecordCount(dealerEmail, startDate, endDate, targetStatusTypes, true);
	}
	
	/**
	 * 
	 * This returns a subquery for the valid basic data ids for this dealer/agent email and time range 
	 * 
	 * @param criteriaBuilder
	 * @param query
	 * @param email
	 * @param startDate
	 * @param endDate
	 * @param dealer
	 * @return
	 */
	public Subquery<Long> getValidBasicDataIdsSubQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, String email, 
			Date startDate, Date endDate, boolean dealer){
		
		Subquery<Long> basicDataSubQuery = query.subquery(Long.class);
		Root<BasicData> root = basicDataSubQuery.from(BasicData.class);
		
//		time range of capture subquery
		Subquery<String> subquerySar = basicDataSubQuery.subquery(String.class);
		Root<SmsActivationRequest> subRootSar = subquerySar.from(SmsActivationRequest.class);
		
		Predicate targetRegPeriodCondition = criteriaBuilder.between(subRootSar.get("receiptTimestamp"), startDate, endDate);

		subquerySar.select(subRootSar.get("uniqueId"));
		subquerySar.where(targetRegPeriodCondition);
		
//		dealer's agents email addresses subquery
		Subquery<String> subqueryKmuser = basicDataSubQuery.subquery(String.class);
		Root<KMUser> subRootKmuser = subqueryKmuser.from(KMUser.class);
		
		subqueryKmuser.select(criteriaBuilder.lower(subRootKmuser.get("emailAddress")));
		
		String targetEmail = (email != null) ? email.toLowerCase() : "";
		
		Predicate emailAddressesCondition;
		
		if(dealer){
			emailAddressesCondition = criteriaBuilder.equal(
					criteriaBuilder.lower(subRootKmuser.get("assignedDealer").get("emailAddress")), 
					targetEmail);
		} else {
			emailAddressesCondition = criteriaBuilder.equal(criteriaBuilder.lower(subRootKmuser.get("emailAddress")), targetEmail);
		}
		
		subqueryKmuser.where(emailAddressesCondition);
		
		
		In<?> uniqueIdCondition = criteriaBuilder.in(root.get("userId").get("uniqueId")).value(subquerySar);
		In<?> emailCondition = criteriaBuilder.in(criteriaBuilder.lower(root.get("biometricCaptureAgent"))).value(subqueryKmuser);
		
		basicDataSubQuery.select(root.get("id"));
		basicDataSubQuery.where(emailCondition, uniqueIdCondition);
		
		return basicDataSubQuery;
	}
	
	public List<Long> getValidBasicDataIds(String email, Date startDate, Date endDate){
		
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<BasicData> root = criteriaQuery.from(BasicData.class);
		
		Subquery<String> subquerySar = criteriaQuery.subquery(String.class);
		Root<SmsActivationRequest> subRootSar = subquerySar.from(SmsActivationRequest.class);
		
		Predicate targetRegPeriodCondition = criteriaBuilder.between(subRootSar.get("registrationTimestamp"), startDate, endDate);

		subquerySar.select(subRootSar.get("uniqueId"));
		subquerySar.where(targetRegPeriodCondition);
		
		Predicate emailCondition = criteriaBuilder.equal(criteriaBuilder.lower(root.get("biometricCaptureAgent")), email.toLowerCase());
		
		In<?> uniqueIdCondition = criteriaBuilder.in(root.get("userId").get("uniqueId")).value(subquerySar);
		
		criteriaQuery.select(root.get("id"));
		criteriaQuery.where(emailCondition, uniqueIdCondition);
		
		return getResultList(entityManager, criteriaQuery);
	}

	/**
	 * @param email
	 * @return
	 */
	public KycDealer getKycDealerByAgentEmail(String agentEmail) {
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<KycDealer> criteriaQuery = criteriaBuilder.createQuery(KycDealer.class);
		Root<KMUser> root = criteriaQuery.from(KMUser.class);
		
		if(agentEmail == null){
			agentEmail = "";
		}
		
		Predicate emailCondition = criteriaBuilder.equal(criteriaBuilder.lower(root.get("emailAddress")), agentEmail.toLowerCase());
		
		criteriaQuery.select(root.get("assignedDealer"));
		criteriaQuery.where(emailCondition);
		
		return getSingleResult(entityManager, criteriaQuery);
	}

	/**
	 * @param email
	 * @param startDate
	 * @param endDate
	 * @param targetStatusTypes
	 * @return
	 */
	public Long getCleanRecordAgentCount(String email, Date startDate, Date endDate, List<StatusType> targetStatusTypes) {
		return getCleanRecordCount(email, startDate, endDate, targetStatusTypes, false);
	}

	/**
	 * @param email
	 * @param startDate
	 * @param endDate
	 * @param targetStatusTypes
	 * @return
	 */
	public Long getCleanRecordCount(String email, Date startDate, Date endDate, List<StatusType> targetStatusTypes, boolean dealer) {
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<ValidationResult> root = criteriaQuery.from(ValidationResult.class);
		
		criteriaQuery.select(criteriaBuilder.count(root.get("pk")));
		
		Predicate statusCondition;
		
		if(targetStatusTypes != null && !targetStatusTypes.isEmpty()){
			statusCondition = root.get(ValidationResult_.approvalStatus).in(targetStatusTypes);
		} else {
			statusCondition = criteriaBuilder.isNull(root.get(ValidationResult_.approvalStatus));
		}
		Predicate basicDataCondition = root.get(ValidationResult_.recordId)
				.in(getValidBasicDataIdsSubQuery(criteriaBuilder, criteriaQuery, email, startDate, endDate, dealer));
		
		criteriaQuery.where(statusCondition, basicDataCondition);
		
		return getSingleResult(entityManager, criteriaQuery);
	}


	
	/**
	 * @param dealerEmail
	 * @return
	 */
	public List<KMUser> getAgents(String dealerEmail) {
		
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<KMUser> criteriaQuery = criteriaBuilder.createQuery(KMUser.class);
		Root<KMUser> root = criteriaQuery.from(KMUser.class);
		
		String targetEmail = (dealerEmail != null) ? dealerEmail.toLowerCase() : "";
		
		Predicate emailAddressesCondition = criteriaBuilder.equal(
				criteriaBuilder.lower(root.get("assignedDealer").get("emailAddress")), 
				targetEmail);
		
		criteriaQuery.select(root);
		criteriaQuery.where(emailAddressesCondition);
		
		return getResultList(entityManager, criteriaQuery); 
	}


	/**
	 * @return
	 */
	public List<KMUser> getAllKMUsers() {
		
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<KMUser> criteriaQuery = criteriaBuilder.createQuery(KMUser.class);
		Root<KMUser> root = criteriaQuery.from(KMUser.class);
		
		criteriaQuery.select(root);
		
		return getResultList(entityManager, criteriaQuery); 
	}
	
}
