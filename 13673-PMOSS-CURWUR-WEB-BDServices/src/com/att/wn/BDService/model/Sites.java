package com.att.wn.BDService.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sites")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sites {

	private ArrayList<SiteInfo> siteInfo;
	
	public ArrayList<SiteInfo> getSiteInfo() {
		return siteInfo;
	}
	public void setSiteInfo(ArrayList<SiteInfo> siteInfo) {
		this.siteInfo = siteInfo;
	}

	public void addSite(SiteInfo siteInfo){
		this.siteInfo.add(siteInfo);
	}

	public Sites(){
		siteInfo = new ArrayList<SiteInfo>();
	}
	
	public Sites(ArrayList<SiteInfo> siteInfo) {
		this.siteInfo = siteInfo;
	}

}
