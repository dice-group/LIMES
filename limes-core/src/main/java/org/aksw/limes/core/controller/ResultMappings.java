package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;

/**
 * @author sherif
 *
 */
public class ResultMappings {
	protected Mapping verificationMapping;
	protected Mapping acceptanceMapping;
	
	/**
	 * Constructor
	 */
	ResultMappings(){
		this.verificationMapping = new MemoryMapping();
		this.acceptanceMapping 	 = new MemoryMapping();
	}
	
	/**
	 * Constructor
	 */
	public ResultMappings(Mapping verificationMapping, Mapping acceptanceMapping) {
		super();
		this.verificationMapping = verificationMapping;
		this.acceptanceMapping = acceptanceMapping;
	}
	
	/**
	 * @return the verification mapping
	 */
	public Mapping getVerificationMapping() {
		return verificationMapping;
	}
	
	/**
	 * @param verificationMapping
	 */
	public void setVerificationMapping(Mapping verificationMapping) {
		this.verificationMapping = verificationMapping;
	}
	
	/**
	 * @return acceptance mapping
	 */
	public Mapping getAcceptanceMapping() {
		return acceptanceMapping;
	}
	
	/**
	 * @param acceptanceMapping
	 */
	public void setAcceptanceMapping(Mapping acceptanceMapping) {
		this.acceptanceMapping = acceptanceMapping;
	}
	
	
}
