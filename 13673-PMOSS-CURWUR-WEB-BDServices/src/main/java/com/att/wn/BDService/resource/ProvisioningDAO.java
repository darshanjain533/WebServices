package com.att.wn.BDService.resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import com.att.wn.BDService.model.BDResponse;
import com.att.wn.BDService.model.SiteInfo;
import com.att.wn.BDService.model.Sites;
import com.att.wn.BDService.pool.ConnectionBidsPool;
import com.att.wn.BDService.pool.ConnectionPool;
import com.att.wn.logger.SingletonLogger;

import ch.qos.logback.classic.Logger;

public class ProvisioningDAO {
	// This class serves as the Database access repository for the Provisioning
	// service
	public static Logger logger = SingletonLogger.getInstance().getLoggerContext().getLogger(ProvisioningDAO.class);

	protected ProvisioningDAO() {

	}
/**
 * sr059n - check if getDomainNames to be pollled from cur mis db
 * @param instarCustId
 * @return
 */
	protected ArrayList<String> getDomainNames(int instarCustId) {
		// do DB work to find the list of domain names
		// add those names to the domainNames arrayList

		String query;
		Statement st;
		ResultSet rs;
		ArrayList<String> domainNames = new ArrayList<String>();
		Connection conn1 = null;

		try {

			// Query CTPORTTABLE for domain names associated wiht the instarCustId
			logger.debug("what is instartcustId... {} ", instarCustId);
			conn1 = ConnectionPool.getCurConnection();
			st = conn1.createStatement();
			// query = "Select distinct PRIMARY_DOMAIN_NAME from ctporttable@bids where
			// PORT_STATE = 'ACTIVE' and cust_id = " + instarCustId;
			query = "select distinct SUB_DOMAIN_NAME from srvc_acc_pt where cust_id = " + instarCustId
					+ " and srvc_acc_pt_id in (select distinct srvc_acc_pt_id from port_asgmnt where cust_id = "
					+ instarCustId + " and record_end_datetime = to_date('99991231', 'yyyymmdd'))";

			logger.debug("The query is: " + query);

			rs = st.executeQuery(query);
			while (rs.next()) {

				// Add the domain names our list
				logger.debug(instarCustId + " matched " + rs.getString(1));
				domainNames.add(rs.getString(1));
			}
		} catch (Exception e) {
			// logCritical("Error in getData " + e);
			logger.debug("Error in companyValidation " + e);
		} finally {
			try {
				if (conn1 != null) {
					conn1.close();
				}
			} catch (Exception e) {
				// logCritical("Error closing connection" + e);
			}
		}

		return domainNames;
	}

	// Get the list of Sites associated with the given domainName
	/**
	 * sr059n - check if getSites to be polled from cur mis db
	 * @param domainName
	 * @return
	 */
	protected Sites getSites(String domainName) {

		String query;
		Statement st;
		ResultSet rs;
		Sites sites = new Sites();
		Connection conn1 = null;

		try {

			// Query CTPORTTABLE for srvc_acc_pt_id (site ids) and srvc_acc_pt_addr
			// associated with the domainName
			
			conn1 = ConnectionPool.getCurConnection();
			st = conn1.createStatement();

			// query = "Select distinct srvc_acc_pt_id, srvc_acc_pt_addr from
			// ctporttable@bids where PORT_STATE = 'ACTIVE' and PRIMARY_DOMAIN_NAME = '" +
			// domainName +"'";
			query = "select sap.srvc_acc_pt_id, sap.srvc_acc_pt_addr from srvc_acc_pt sap, port_asgmnt pa where sap.sub_domain_name = '"
					+ domainName
					+ "' and sap.srvc_acc_pt_id = pa.srvc_acc_pt_id and pa.record_end_datetime = to_date('99991231','yyyymmdd')";

			
				logger.debug("The query is: " + query);

			rs = st.executeQuery(query);

			while (rs.next()) {

				// Add the domain names our list
				logger.debug(domainName + " matched " + rs.getInt(1) + " | " + rs.getString(1));
				
				SiteInfo siteInfo = new SiteInfo();

				siteInfo.setSiteId(rs.getInt(1));
				siteInfo.setSiteAddr(rs.getString(2));
				sites.addSite(siteInfo);
			}
		} catch (Exception e) {
			// logCritical("Error in getData " + e);
			logger.debug("Error in companyValidation " + e);
		} finally {
			try {
				if (conn1 != null) {
					conn1.close();
				}
			} catch (Exception e) {
				// logCritical("Error closing connection" + e);
			}
		}

		return sites;
	}

	// Use the instarCustId to find a matching Cust_Name in INSTAR's ctporttable
	// If found return it, otherwise show an error
	/**
	 * 
	 * sr059n - company validation is polled from BIDS Instar DB
	 * @param instarCustId
	 * @param primaryDomainName
	 * @return
	 */
	protected BDResponse companyValidation(int instarCustId, String primaryDomainName) {

		String query;
		Statement st;
		ResultSet rs;

		// Set up a default response
		BDResponse response = new BDResponse();
		response.setValidationCode("ERROR");
		response.setValidationMessage("The INSTAR CUST ID wasn't found in INSTAR's provisioning table!");
		Connection connBids = null;

		try {

			// Now try to find out if there is a matching entry in CTPORTTABLE
			// sep -2021 -sr059n - took bids connection for INSTAR bids db access viz JDBC
			connBids = ConnectionBidsPool.getBidsConnection();
			st = connBids.createStatement();

			/*
			 * commenting out if DB decision swithces back to oracle from postgres query =
			 * "Select cust_name from ctporttable@bids where PORT_STATE = 'ACTIVE' and cust_id = "
			 * + instarCustId + " and primary_domain_name = '" + primaryDomainName + "'";
			 */
			query = "Select cust_name from ctporttable where PORT_STATE = 'ACTIVE' and cust_id = " + instarCustId
					+ " and primary_domain_name = '" + primaryDomainName + "'";
			logger.debug("The query is: " + query);

			rs = st.executeQuery(query);
			while (rs.next()) {

				// We found something! Let's replace that default with some success

				response.setCustomerName(rs.getString(1));
				logger.debug(instarCustId + " matched " + response.getCustomerName());
				response.setValidationCode("OK");
				response.setValidationMessage("instarCustId Matched!");
			}
		} catch (Exception e) {
			// logCritical("Error in getData " + e);
			logger.debug("Error in companyValidation " + e);
			response.setValidationMessage("CURWUR has encountered an error during validation");
		} finally {
			try {
				if (connBids != null) {
					connBids.close();
				}
			} catch (Exception e) {
				// logCritical("Error closing connection" + e);
			}
		}

		return response;
	}

}
