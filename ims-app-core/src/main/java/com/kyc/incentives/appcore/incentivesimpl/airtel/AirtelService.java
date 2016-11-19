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
import javax.persistence.metamodel.SingularAttribute;

import com.kyc.incentives.airtel.AirtelIncentivePayment;
import com.kyc.incentives.airtel.AirtelIncentivePayment_;
import com.kyc.incentives.appcore.contracts.pojo.RegCountPojo;
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

//	/**
//	 * @param name
//	 * @param months
//	 * @return
//	 */
//	public Long get100To200BandCount(String name, List<String> months, boolean dealer) {
//		
//		EntityManager entityManager = getEntityManager();
//		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//		
//		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
//		Root<AirtelIncentivePayment> root = criteriaQuery.from(AirtelIncentivePayment.class);
//		
//		Predicate monthCondition = root.get(AirtelIncentivePayment_.month).in(months);
//		Predicate notNullCondition;
//		
//		Predicate nameCondition;
//		
//		if(dealer){
//			nameCondition = criteriaBuilder.equal(root.get(AirtelIncentivePayment_.dealer), name);
//			notNullCondition = criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band100));
//		} else {
//			nameCondition = criteriaBuilder.equal(root.get(AirtelIncentivePayment_.fsa), name);
//			notNullCondition = criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band100));
//		}
//		
//		criteriaQuery.select(root.get(AirtelIncentivePayment_.band100));
//		criteriaQuery.where(monthCondition, nameCondition, notNullCondition);
//		
////		String band100 = getSingleResult(entityManager, criteriaQuery);
//		
//		List<String> resultList = getResultList(entityManager, criteriaQuery);
//		
//		if(resultList == null){
//			return 0L;
//		}
//		
//		long totalCount = 0;
//		
//		for(String val : resultList){
//			totalCount += Long.valueOf(val);
//		}
//		
//		return totalCount;
//	}

	/**
	 * @param name
	 * @param months
	 * @param dealer
	 * @param targetBand 
	 * @return
	 */
	public List<RegCountPojo> getDealerAgentCounts(String name, List<String> months, boolean dealer, SingularAttribute<AirtelIncentivePayment, Long> targetBand) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<RegCountPojo> criteriaQuery = criteriaBuilder.createQuery(RegCountPojo.class);
		Root<AirtelIncentivePayment> root = criteriaQuery.from(AirtelIncentivePayment.class);
		
		Predicate monthCondition = root.get(AirtelIncentivePayment_.month).in(months);
		Predicate notNullCondition;
		
		notNullCondition = criteriaBuilder.and(criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band100))
				, criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band200))
				, criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band500))
				, criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.band1000))
				, criteriaBuilder.isNotNull(root.get(AirtelIncentivePayment_.vendorType))
				) ;
		
		Predicate nameCondition;
		
		if(dealer){
			nameCondition = criteriaBuilder.equal(root.get(AirtelIncentivePayment_.dealer), name);
		} else {
			nameCondition = criteriaBuilder.equal(root.get(AirtelIncentivePayment_.fsa), name);
		}
		
		
		criteriaQuery.select(criteriaBuilder.construct(RegCountPojo.class
				, root.get(AirtelIncentivePayment_.dealer)
				, root.get(AirtelIncentivePayment_.fsa)
				, root.get(AirtelIncentivePayment_.vendorType)
				, criteriaBuilder.coalesce(criteriaBuilder.sum(root.get(targetBand)), Long.valueOf(0)) 
				));		
		
		criteriaQuery.where(monthCondition, nameCondition, notNullCondition);
		criteriaQuery.groupBy(root.get(AirtelIncentivePayment_.fsa)
				, root.get(AirtelIncentivePayment_.dealer)
				, root.get(AirtelIncentivePayment_.vendorType));
		
		return getResultList(entityManager, criteriaQuery);
	}
	
}
