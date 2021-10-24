package com.att.wn.BDService.pool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import com.att.wn.logger.SingletonLogger;
import ch.qos.logback.classic.Logger;

/*
*	History		
*	Date			Who		Why?
*

27-Aug-2021       sr059n   ConnectionPool Enhanced
//==================================================================================================================================
/*
* this class is used to fetch connection only for MIS postgres NA for Bids
* 
* this class uses two types of connection. 
* 	-> call the AzKeyvaultSecretService to retrieve the password from vault or leverage azure env variables that reads keyvault secrets with specific configuration
*/

public class ConnectionBidsPool {

	public static Logger logger = SingletonLogger.getInstance().getLoggerContext().getLogger(ConnectionBidsPool.class);
	private static String connectionProps = "connectionbidspool.properties";
	
	private static String jdbc = null;
	private static String host = null;
	private static String port = null;
	private static String sid = null;
	private static String username = null;
	private static String keyvaultName = null;
	private static String secretKeywtVer = null;
	private static String password = null;
	private static boolean azSecretFlag = false;
	private static String driver = null;
	// for type-1
	private static boolean getJdbcProperties() {
		InputStream reader = null;
		boolean propsT1LoadFlag = false;

		try {
			logger.debug("Type 1 , Fetching Properties for the BIDS Database (ORACLE EXT) ... ");
			//logger.debug("Get JDBC Properties ... ");
			//Properties properties = new Properties();
			//logger.debug("Load the properties file viz resource as stream ");		
			//reader = ConnectionBidsPool.class.getClassLoader().getResourceAsStream(connectionProps);
			//logger.debug("Fetching properties now........ "); 
			//properties.load(reader);
			logger.debug("Loading .................... ");	
			driver = "oracle.jdbc.driver.OracleDriver";
			jdbc = System.getenv("oracle_jdbc");
			host = System.getenv("bids_host");
			port = System.getenv("bids_port");
			sid = System.getenv("bids_sid");
			username = System.getenv("bids_username");
			password = System.getenv("bids_password");
			/*jdbc = properties.getProperty("JDBC");
			host = properties.getProperty("HOST");
			port = properties.getProperty("PORT");
			sid = properties.getProperty("SID");
			username = properties.getProperty("USERNAME");
			keyvaultName = properties.getProperty("KEYVAULTNAME");
			secretKeywtVer = properties.getProperty("SECRETKEYWITHVERSION");
			*/
			logger.debug("jdbc is {}........ ", jdbc);
			logger.debug("host is {}........ ", host);
			logger.debug("port is {}........ ", port);
			logger.debug("sid is {}........ ", sid);
			logger.debug("username is {}........ ", username);
			logger.debug("Done Loading .................... ");
			logger.debug(
					"Now the properties are loaded , before fetching JDBC connection we must retrieve password from keyvault using vault name and secret ");
			/* call the AzSecretService
			if (StringUtils.isNotBlank(keyvaultName) && StringUtils.isNotBlank(secretKeywtVer)) {
				logger.debug("Keyvault Name and SecretKeyWtVer is not blank... calling azkvservice.... ");
				retrievedSecretObj = AZKeyvaultSecretService.retrieveSecret(keyvaultName, secretKeywtVer);
			} else {
				logger.debug("Keyvault Name and SecretKeyWtVer is blank... skip calling azkvservice.... ");
				azSecretFlag = false;
			}
			if (null != retrievedSecretObj) {
				password = retrievedSecretObj.getValue();
				if (StringUtils.isNotBlank(password)) {
					azSecretFlag = true;
				} else {
					azSecretFlag = false;
				}
			} else {
				logger.debug("Problem occured in fetching secret from keyvault....");
				azSecretFlag = false;
			}*/
			// check other props also for null here and raise / down flag
			if (StringUtils.isNotBlank(jdbc) && StringUtils.isNotBlank(host) && StringUtils.isNotBlank(port) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
				propsT1LoadFlag = true;
			} else {
				propsT1LoadFlag = false;
			}
		} catch (Exception ex) {
			propsT1LoadFlag = false;
			System.out.println("Exception caught");
			System.out.println("Error : " + ex.getMessage());
		}  finally {
			/*
			 * if (null != reader) { try { reader.close(); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } }
			 */
		}
		return propsT1LoadFlag;
	}

	// for type-2
	
	// type-1 - loads the connection viz props file from resources
	public static synchronized Connection getBidsConnection() {	
		if(getJdbcProperties()) {
		try {
			Connection bidsConnection = null;
			String uri = jdbc + "@" + host + ":" + port + ":" + sid;
			logger.debug("Database URL : " + uri);
			 Class.forName(driver);
			bidsConnection = DriverManager.getConnection(uri,username,password);
				return bidsConnection;			
			} catch (Exception e) {
				logger.debug("Exception caught in JDBC Connection Pool");
				logger.debug(e.getMessage());
				return null;
			}
		}else {
			logger.debug("Error in getting Database information in getCurConnection method");
			return null;
		}
		
	}
}
