/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.kyc.incentives.appcore.service.ImsService;
import com.sf.biocapture.entity.HeartBeat;
import com.sf.biocapture.entity.NodeAssignment;

/**
 * @author dawuzi
 *
 */
public class KitActivityService extends ImsService {

	/**
	 * static Singleton instance
	 */
	private static KitActivityService instance;

	/**
	 * Private constructor for singleton
	 */
	private KitActivityService() {
	}

	/**
	 * Static getter method for retrieving the singleton instance
	 */
	public static KitActivityService getInstance() {
		if (instance == null) {
			instance = new KitActivityService();
		}
		return instance;
	}

	/**
	 * @param email
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Long getActiveKitCount(String email, Date startDate, Date endDate) {
		
		if(email == null || email.trim().isEmpty()){
			return 0L;
		}
		
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<HeartBeat> root = criteriaQuery.from(HeartBeat.class);
		
//		the mac address match subquery
		Subquery<String> subqueryNodeAssignment = criteriaQuery.subquery(String.class);
		Root<NodeAssignment> subRootNodeAssignment = subqueryNodeAssignment.from(NodeAssignment.class);
		
		Predicate emailCondition = criteriaBuilder.equal(criteriaBuilder.lower(subRootNodeAssignment.get("assignedDealer").get("emailAddress")), email.toLowerCase());
		
		subqueryNodeAssignment.select(subRootNodeAssignment.get("targetNode").get("macAddress"));
		subqueryNodeAssignment.where(emailCondition);
		
		
		Predicate macAddressCondition = root.get("macAddress").in(subqueryNodeAssignment);
		Predicate heartBeartTimeRangeCondition = criteriaBuilder.between(root.get("receiptTimestamp"), 
				getSqlTimeStamp(startDate), getSqlTimeStamp(endDate));
		
		
		criteriaQuery.select(criteriaBuilder.countDistinct(root.get("macAddress")));
		criteriaQuery.where(heartBeartTimeRangeCondition, macAddressCondition);
		
		return getSingleResult(entityManager, criteriaQuery);
	}
	
	/**
	 * @param date
	 * @return
	 */
	private Timestamp getSqlTimeStamp(Date date) {
		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp;
	}
}
