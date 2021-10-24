package com.att.wn.BDService.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class Domain {
	
	@XmlElement(name = "domainname")
	private String domainName;
	private Sites sites;
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public Sites getSites() {
		return sites;
	}
	public void setSites(Sites sites) {
		this.sites = sites;
	}
	
	public Domain() {
		this.domainName = "default.com";
		this.sites = new Sites();
	}
	
	
	public Domain(String domainName, Sites sites) {
		this.domainName = domainName;
		this.sites = sites;
	}
	
	

}
