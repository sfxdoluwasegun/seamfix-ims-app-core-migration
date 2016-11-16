/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.kyc.incentives.airtel.AirtelIncentivePayment;
import com.kyc.incentives.airtel.AirtelIncentivePayment_;
import com.kyc.incentives.appcore.service.ImsService;

/**
 * @author dawuzi
 *
 */
public class AirtelService extends ImsService {

	/**
	 * static Singleton instance
	 */
	private static AirtelService instance;

	/**
	 * Private constructor for singleton
	 */
	private AirtelService() {
	}

	/**
	 * Static getter method for retrieving the singleton instance
	 */
	public static AirtelService getInstance() {
		if (instance == null) {
			instance = new AirtelService();
		}
		return instance;
	}

	/**
	 * @param name
	 * @param months
	 * @return
	 */
	public Long get100To200BandCount(String name, List<String> months, boolean dealer) {
		
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
		Root<AirtelIncentivePayment> root = criteriaQuery.from(AirtelIncentivePayment.class);
		
		Predicate monthCondition = root.get(AirtelIncentivePayment_.month).in(months);
		Predicate notNullCondition;
		
		Predicate nameCondition;
		
		if(dealer){
			nameCondition = criteriaBuilder.equal(root.get(AirtelIncentivePayment_.dealer), name);
			notNullCondition = criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band100));
		} else {
			nameCondition = criteriaBuilder.equal(root.get(AirtelIncentivePayment_.fsa), name);
			notNullCondition = criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band100));
		}
		
		criteriaQuery.select(root.get(AirtelIncentivePayment_.band100));
		criteriaQuery.where(monthCondition, nameCondition, notNullCondition);
		
//		String band100 = getSingleResult(entityManager, criteriaQuery);
		
		List<String> resultList = getResultList(entityManager, criteriaQuery);
		
		if(resultList == null){
			return 0L;
		}
		
		long totalCount = 0;
		
		for(String val : resultList){
			totalCount += Long.valueOf(val);
		}
		
		return totalCount;
	}
	
}
