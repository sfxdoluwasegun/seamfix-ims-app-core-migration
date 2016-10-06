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
import com.sf.biocapture.entity.SmsActivationRequest;
import com.sf.biocapture.entity.enums.StatusType;
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
	 * @param email
	 * @param startDate
	 * @param endDate
	 */
	public Long getCleanRecordCount(String email, Date startDate, Date endDate) {
		
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<ValidationResult> root = criteriaQuery.from(ValidationResult.class);
		
		criteriaQuery.select(criteriaBuilder.count(root.get("pk")));
		
		Predicate nameCondition = criteriaBuilder.equal(root.get(ValidationResult_.approvalStatus), StatusType.PASSED); 
		Predicate basicDataCondition = root.get(ValidationResult_.recordId)
				.in(getValidBasicDataIdsSubQuery(criteriaBuilder, criteriaQuery, email, startDate, endDate));
		
		criteriaQuery.where(nameCondition, basicDataCondition);
		
		return getSingleResult(entityManager, criteriaQuery);
		
	}
	
	public Subquery<Long> getValidBasicDataIdsSubQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, String email, Date startDate, Date endDate){
		
		Subquery<Long> basicDataSubQuery = query.subquery(Long.class);
		Root<BasicData> root = basicDataSubQuery.from(BasicData.class);
		
		Subquery<String> subquerySar = basicDataSubQuery.subquery(String.class);
		Root<SmsActivationRequest> subRootSar = subquerySar.from(SmsActivationRequest.class);
		
		Predicate targetRegPeriodCondition = criteriaBuilder.between(subRootSar.get("registrationTimestamp"), startDate, endDate);

		subquerySar.select(subRootSar.get("uniqueId"));
		subquerySar.where(targetRegPeriodCondition);
		
		Predicate emailCondition = criteriaBuilder.equal(criteriaBuilder.lower(root.get("biometricCaptureAgent")), email.toLowerCase());
		
		In<?> uniqueIdCondition = criteriaBuilder.in(root.get("userId").get("uniqueId")).value(subquerySar);
		
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
	
}
