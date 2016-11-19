/**
 * 
 */
package com.kyc.incentives.appcore.contracts.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author dawuzi
 *
 */

@Getter
@Setter
@ToString
public class RegCountPojo {

	private String dealer;
	private String agent;
	private String vendorType;
	private Long count;
	
	public RegCountPojo(String dealer, String agent, String vendorType, Long count) {
		this.dealer = dealer;
		this.agent = agent;
		this.vendorType = vendorType;
		this.count = count;
	}

	public RegCountPojo() {
	}
}
