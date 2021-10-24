package com.att.wn.BDService.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


// This class represents a response to a BD User provisioning request


@XmlRootElement(name = "bduserprovresponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BDUserProvResponse {
	
	@XmlElement(name = "validationcode")
	private String validationCode;
	@XmlElement(name = "validationmessage")
	private String validationMessage;
	@XmlElement(name = "domaininfo")
	private DomainInfo domainInfo;
	
	public String getValidationCode() {
		return validationCode;
	}

	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}

	public DomainInfo getDomainInfo() {
		return domainInfo;
	}

	public void setDomainInfo(DomainInfo domainInfo) {
		this.domainInfo = domainInfo;
	}

	public BDUserProvResponse() {
        validationCode = "ERROR";
        validationMessage = "There were no domains or sites associated with this InstarCustIDt";
        domainInfo = new DomainInfo();
    }
 
    public BDUserProvResponse(String validationCode, String validationMessage, DomainInfo domainInfo) {

        this.validationCode = validationCode;
        this.validationMessage = validationMessage;
        this.domainInfo = domainInfo;
    }
	
	

}
