package com.att.wn.BDService.model;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "domaininfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainInfo {

	private Vector<Domain> domain;

	public Vector<Domain> getDomain() {
		return domain;
	}

	public void setDomain(Vector<Domain> domain) {
		this.domain = domain;
	}
	
	public void addDomain(Domain domain){
		this.domain.add(domain);
	}
	
	public DomainInfo() {
        domain = new Vector<Domain>();
    }
 
    public DomainInfo(Vector<Domain> domain) {

	     this.domain = domain;
    }

}
