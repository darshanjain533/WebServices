package com.att.wn.BDService.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Vector;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;
import com.att.wn.BDService.model.*;
import com.att.wn.logger.SingletonLogger;

import ch.qos.logback.classic.Logger;

// This class routes and manages the services interacting with BD's company and customer provisioning

@Path("/provisioning")
public class ProvisioningService {
	public static Logger logger = SingletonLogger.getInstance().getLoggerContext().getLogger(ProvisioningService.class);
	static ProvisioningDAO dao = new ProvisioningDAO();
	// private BDResponse BDResponse = new BDResponse("OK", "This cust_id exists",
	// "Dave's Games");
	private BDCompProvRequest BDCompProvRequest = new BDCompProvRequest("DavesSystem", "pass", 13, "dave.com");

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	// Basic "is the service running" test
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String respondAsReady() {
		return "Provsioning service is ready!";
	}

	@GET
	@Path("sample")
	@Produces(MediaType.APPLICATION_XML)
	// @XmlHeader("<?xml-stylesheet?>")
	public BDCompProvRequest getSampleDomain() {
		logger.debug("----BDCompProvRequest > getSampleDomain --- ");
		return BDCompProvRequest;
	}

	// Consume BD's company provisioning request and respond with a BDResponse
	@POST
	@Path("company")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public BDResponse companyProvisioning(BDCompProvRequest compProvRequest) {

		String system = compProvRequest.getSystem();
		String password = compProvRequest.getPassword();
		String primaryDomain = compProvRequest.getPrimaryDomain();
		int instarCustId = compProvRequest.getInstarCustId();
		logger.debug("---- BDResponse > companyProvisioning --- ");
		logger.debug("---- BDResponse > companyProvisioning --- system {}  ", system);
		logger.debug("---- BDResponse > companyProvisioning --- password {} ", password);
		logger.debug("---- BDResponse > companyProvisioning --- primaryDomain {} ", primaryDomain);
		logger.debug("---- BDResponse > companyProvisioning --- instarCustId {}", instarCustId);
		logger.debug("++++ BDResponse > companyProvisioning ++++");
		logger.debug("Company prov requested " + system + " " + password + "  " + primaryDomain + "  " + instarCustId);

		logger.debug("---- Dao Call for Company Validation ----");
		logger.debug("---- dao obj > companyValidation with (instarcustid, primarydomain)  ---- {} ,  {} ",
				instarCustId, primaryDomain);
		BDResponse response = dao.companyValidation(instarCustId, primaryDomain);
		logger.debug("---- BDResponse responsed back with ---- response obj {} ", response);
		return response;
	}

	// Consume BD's user provisioning request and respond with a list of domains and
	// sites associated with their instarCustId
	@POST
	@Path("user")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public BDUserProvResponse userProvisioning(BDUserProvRequest userProvRequest) {
		logger.debug("---- BDUserProvResponse > userProvisioning --- ");
		BDUserProvResponse response = new BDUserProvResponse();
		DomainInfo domainInfo = new DomainInfo();

		// Read the values from the request
		String system = userProvRequest.getSystem();
		logger.debug("---- BDUserProvResponse > system --- {}",system);
		String password = userProvRequest.getPassword();
		logger.debug("---- BDUserProvResponse >  password---{} ",password);
		String primaryDomain = userProvRequest.getPrimaryDomain();
		logger.debug("---- BDUserProvResponse > primaryDomain ---{} ",primaryDomain);
		int instarCustId = userProvRequest.getInstarCustId();
		logger.debug("User prov requested " + system + " " + password + "  " + primaryDomain + "  " + instarCustId);
		logger.debug("Domain Info Set Domain by getDomains call with instarCustid as => " + instarCustId);
		domainInfo.setDomain(getDomains(instarCustId, "BDUserProvResponse:userProvisioning"));

		if (!domainInfo.getDomain().isEmpty()) {
			logger.debug("Domain Info Set Domain by getDomains is not empty for the instarCustID ==> " + instarCustId);
			if (!domainInfo.getDomain().get(0).getDomainName().equals("default.com")) {
				logger.debug("Domain Info Set Domain by getDomains is not equla to \"deafult.com\" for the instarCustID ==> " + instarCustId);
				response.setValidationCode("OK");
				response.setValidationMessage("User provisioning successfuL!");
				response.setDomainInfo(domainInfo);
			}
		}
		logger.debug("---- BDUserProvResponse : responsed back with ---- response obj {} ", response);
		return response;
	}

	// Get and populate the Domains associated with the given instarCustId
	private Vector<Domain> getDomains(int instarCustId, String calledFrom) {
		logger.debug("---- getDomains method (called from BDUserProvResponse???) "+calledFrom);
		Vector<Domain> domains = new Vector<Domain>();
		ArrayList<String> domainNames = new ArrayList<String>();

		// get the domainNames associated with the instarCustId
		logger.debug("----  get the domainNames associated with the instarCustId "+instarCustId);
		logger.debug("----  dao call......");
		domainNames = dao.getDomainNames(instarCustId);
		logger.debug("----  dao call returned domain names ???? ......");
		logger.debug("----  domain names >> ......"+domainNames);
		logger.debug("----  lopp throught the vector domain names and call to domainFiller thread class is made in loop >> ......");
		int count = 0;
		int numThreads = 100;
		Thread[] t = new Thread[numThreads];

		for (String domainName : domainNames) {
			// Fill in the domain info

			// Because some custIDs can have many domains, let's resolve this in parallel
			try {
				t[count] = new Thread(new domainFiller(domainName, domains));
				t[count].start();
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (count == (numThreads)) {
				count--;
				while (count > -1) {
					try {
						t[count].join();
					} catch (Exception e) {
						e.printStackTrace();
					}
					count--;
				}
				count = 0;
			}
		}

		while (count > 0) {
			try {
				count--;
				t[count].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return domains;
	}

	private class domainFiller implements Runnable {
		// Class to handle the job of getting sites via threading
		// Basically just call getSites and update the Vector

		String domainName;
		Sites curSites;
		Vector<Domain> domains;

		domainFiller(String domainName, Vector<Domain> domains) {
			curSites = new Sites();
			this.domainName = domainName;
			this.domains = domains;
		}

		public void run() {
			logger.debug("inside run method..");
			logger.info("--- run --- call to dao......");
			logger.debug("--- run --- call to dao getSites......");
			curSites = dao.getSites(domainName);
			logger.debug("--- run --- After getSites call we must get back the curSites ........ {} ",curSites);
			logger.info("finished getting Sites for:" + domainName);
			Domain curDomain = new Domain(domainName, curSites);
			domains.add(curDomain);

		}
	}

}