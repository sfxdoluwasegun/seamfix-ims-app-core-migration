/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.kyc.incentives.appcore.service.ImsService;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.NodeAssignment;
import com.sf.biocapture.entity.SmsActivationRequest;

/**
 * @author dawuzi
 *
 */
public class ActiveDevicesService extends ImsService {

	/**
	 * static Singleton instance
	 */
	private static ActiveDevicesService instance;

	/**
	 * Private constructor for singleton
	 */
	private ActiveDevicesService() {
	}

	/**
	 * Static getter method for retrieving the singleton instance
	 */
	public static ActiveDevicesService getInstance() {
		if (instance == null) {
			instance = new ActiveDevicesService();
		}
		return instance;
	}

	/**
	 * @param email
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Long getActiveDevicesCount(String email, Date startDate, Date endDate) {
		
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<NodeAssignment> root = criteriaQuery.from(NodeAssignment.class);
		
		criteriaQuery.select(criteriaBuilder.countDistinct(root.get("pk")));
		
		
		
//		time range of capture subquery
		Subquery<String> subquerySar = criteriaQuery.subquery(String.class);
		Root<SmsActivationRequest> subRootSar = subquerySar.from(SmsActivationRequest.class);
		
		Predicate targetRegPeriodCondition = criteriaBuilder.between(subRootSar.get("receiptTimestamp"), startDate, endDate);

		subquerySar.select(subRootSar.get("enrollmentRef"));
		subquerySar.where(targetRegPeriodCondition);
		
		
//		the mac address match subquery
		Subquery<String> subqueryEnrollmentRef = criteriaQuery.subquery(String.class);
		Root<EnrollmentRef> subRootEnrollmentRef = subqueryEnrollmentRef.from(EnrollmentRef.class);
		
		Predicate enrollmentRefCodeCondition = subRootEnrollmentRef.get("code").in(subquerySar);		

		subqueryEnrollmentRef.select(subRootEnrollmentRef.get("macAddress"));
		subqueryEnrollmentRef.where(enrollmentRefCodeCondition);
		
		
		
		Predicate emailCondition = criteriaBuilder.equal(criteriaBuilder.lower(root.get("assignedDealer").get("emailAddress")), email.toLowerCase());
		Predicate macAddressCondition = root.get("targetNode").get("macAddress").in(subqueryEnrollmentRef);
		
		criteriaQuery.where(emailCondition, macAddressCondition);
		
		return getSingleResult(entityManager, criteriaQuery);
	}
	
	
}
