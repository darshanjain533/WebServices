package com.att.wn.BDService.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//This class represents a BDRequest for either Userany or user provisioning

@XmlRootElement(name = "bduserprovrequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class BDUserProvRequest {
	
	private String system;
	private String password;
	@XmlElement(name = "instarcustid")
	private int instarCustId;
	@XmlElement(name = "primarydomain")
	private String primaryDomain;
	
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getInstarCustId() {
		return instarCustId;
	}
	public void setInstarCustId(int instarCustId) {
		this.instarCustId = instarCustId;
	}
	public String getPrimaryDomain() {
		return primaryDomain;
	}
	public void setPrimaryDomain(String primaryDomain) {
		this.primaryDomain = primaryDomain;
	}
	
	public BDUserProvRequest(){
		system = "default";
		password = "default";
		instarCustId = -1;
		primaryDomain = "default.com";
	}
	
	public BDUserProvRequest(String system, String password, int instarCustId, String primaryDomain){
		this.system = system;
		this.password = password;
		this.instarCustId = instarCustId;
		this.primaryDomain = primaryDomain;
	}
	
	

}
