package com.att.wn.BDService.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "siteinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteInfo {

	@XmlElement(name = "siteid")
	private int siteId;
	@XmlElement(name = "siteaddr")
	private String siteAddr;
	
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public String getSiteAddr() {
		return siteAddr;
	}
	public void setSiteAddr(String siteAddr) {
		this.siteAddr = siteAddr;
	}
	
	public SiteInfo() {
       	siteId = -1;
       	siteAddr = "nowhere";
    }
 
    public SiteInfo(int siteId, String siteAddr) {

	     this.siteId = siteId;
	     this.siteAddr = siteAddr;
    }
}
