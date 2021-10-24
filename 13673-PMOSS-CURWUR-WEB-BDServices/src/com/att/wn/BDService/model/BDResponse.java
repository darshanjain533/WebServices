package com.att.wn.BDService.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
 

//This class represents a response to a BD Company Provisioning request
//It provides a company name and an OK or ERROR code


@XmlRootElement(name = "bdresponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BDResponse {
	
	@XmlElement(name = "validationcode")
    private String validationCode;
	@XmlElement(name = "validationmessage")
	private String validationMessage;
	@XmlElement(name = "customername")
    private String customerName;
    
    
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
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
    
     
    public BDResponse() {
        validationCode = "";
        validationMessage = "";
        customerName = "";
         
    }
 
    public BDResponse(String validationCode, String validationMessage, String customerName) {

        this.validationCode = validationCode;
        this.validationMessage = validationMessage;
        this.customerName = customerName;
    }

         
}