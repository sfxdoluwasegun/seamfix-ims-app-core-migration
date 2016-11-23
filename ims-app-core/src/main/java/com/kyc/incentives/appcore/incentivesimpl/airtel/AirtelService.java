/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl.airtel;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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

	/**
	 * @param name
	 * @param months
	 * @param dealer
	 * @param targetBand 
	 * @return
	 */
	public List<RegCountPojo> getDealerAgentCounts(String name, List<String> months, boolean dealer, SingularAttribute<AirtelIncentivePayment, Long> targetBand) {
	
		if(targetBand.equals(AirtelIncentivePayment_.variance)){
			return getVarianceDealerAgentCounts(name, months, dealer);
		}
		
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<RegCountPojo> criteriaQuery = criteriaBuilder.createQuery(RegCountPojo.class);
		Root<AirtelIncentivePayment> root = criteriaQuery.from(AirtelIncentivePayment.class);
		
		Predicate monthCondition = root.get(AirtelIncentivePayment_.month).in(months);
		
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
		
		criteriaQuery.where(monthCondition, nameCondition);
		criteriaQuery.groupBy(root.get(AirtelIncentivePayment_.fsa)
				, root.get(AirtelIncentivePayment_.dealer)
				, root.get(AirtelIncentivePayment_.vendorType));
		
		return getResultList(entityManager, criteriaQuery);
	}

	/**
	 * @param name
	 * @param months
	 * @param dealer
	 * @return
	 */
	private List<RegCountPojo> getVarianceDealerAgentCounts(String name, List<String> months, boolean dealer) {

		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<RegCountPojo> criteriaQuery = criteriaBuilder.createQuery(RegCountPojo.class);
		Root<AirtelIncentivePayment> root = criteriaQuery.from(AirtelIncentivePayment.class);
		
		Predicate monthCondition = root.get(AirtelIncentivePayment_.month).in(months);
		
		Expression<Long> sum1 = criteriaBuilder.sum(root.get(AirtelIncentivePayment_.band100), root.get(AirtelIncentivePayment_.band200));
		Expression<Long> sum2 = criteriaBuilder.sum(root.get(AirtelIncentivePayment_.band500), root.get(AirtelIncentivePayment_.band1000));
		Expression<Long> sum3 = criteriaBuilder.sum(root.get(AirtelIncentivePayment_.postpaidLines), root.get(AirtelIncentivePayment_.variance));
		
		Expression<Long> sum = criteriaBuilder.sum(criteriaBuilder.sum(sum1, sum2), sum3);
		
		Predicate varianceAffectedCondition = criteriaBuilder.equal(sum, root.get(AirtelIncentivePayment_.totalRechargelessReregistered));
		
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
				, criteriaBuilder.coalesce(criteriaBuilder.sum(root.get(AirtelIncentivePayment_.variance)), Long.valueOf(0)) 
				));		
		
		criteriaQuery.where(monthCondition, nameCondition, varianceAffectedCondition);
		
		criteriaQuery.groupBy(root.get(AirtelIncentivePayment_.fsa)
				, root.get(AirtelIncentivePayment_.dealer)
				, root.get(AirtelIncentivePayment_.vendorType));
		
		return getResultList(entityManager, criteriaQuery);
	}
	
}
