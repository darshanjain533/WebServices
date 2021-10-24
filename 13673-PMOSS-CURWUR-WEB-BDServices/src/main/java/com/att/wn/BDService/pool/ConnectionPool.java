package com.att.wn.BDService.pool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import com.att.wn.logger.SingletonLogger;
import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;
/*
*	History		
*	Date			Who		Why?
*

27-Aug-2021       sr059n   ConnectionPool Enhanced
//==================================================================================================================================
/*
* this class is used to fetch connection (only) for MIS postgres NA for Bids
* 
* this class uses two types of connection. 
* -> call the AzKeyvaultSecretService to retrieve the password from vault or leverage azure env variables that reads keyvault secrets with specific configuration
*/

public class ConnectionPool {
	public static Logger logger = SingletonLogger.getInstance().getLoggerContext().getLogger(ConnectionPool.class);	
	private static String connectionProps = "connectioncurpool.properties";
	private static String driver = null;
	private static String jdbc = null;
	private static String host = null;
	private static String port = null;
	private static String sid = null;
	private static String username = null;
	private static String keyvaultName = null;
	private static String secretKeywtVer = null;
	private static String password = null;
	private static boolean azSecretFlag = false;

	// for type-1
	private static boolean getJdbcProperties() {
		InputStream reader = null;
		boolean propsT1LoadFlag = false;

		try {
			logger.debug("Type 1 , Fetching Properties for the Cur MIS Database (postgres) ... ");
			logger.debug("Get JDBC Properties ... ");
			//Properties properties = new Properties();
			//logger.debug("Load the properties file viz resource as stream ");
			//sr059n- for web we will load from the src/main/resources
			//reader = ConnectionPool.class.getClassLoader().getResourceAsStream(connectionProps);
			//below one is mis it loads from file system
			//reader = new FileInputStream(new File("/opt/app/mis/resources/" + connectionProps));
			//logger.debug("Fetching properties now........ ");
			//properties.load(reader);
			logger.debug("Loading .................... ");
			driver = System.getenv("postgre_driver");
			jdbc = System.getenv("postgre_jdbc");
			host = System.getenv("wur_host");
			port = System.getenv("wur_port");
			sid = System.getenv("wur_sid");
			username = System.getenv("wur_username");
			password = System.getenv("wur_password");
			/*driver = properties.getProperty("DRIVER");
			jdbc = properties.getProperty("JDBC");
			host = properties.getProperty("HOST");
			port = properties.getProperty("PORT");
			sid = properties.getProperty("SID");
			username = properties.getProperty("USERNAME");
			keyvaultName = properties.getProperty("KEYVAULTNAME");
			secretKeywtVer = properties.getProperty("SECRETKEYWITHVERSION");
			*/
			logger.debug("driver is {}........ ", driver);
			logger.debug("jdbc is {}........ ", jdbc);
			logger.debug("host is {}........ ", host);
			logger.debug("port is {}........ ", port);
			logger.debug("sid is {}........ ", sid);
			logger.debug("username is {}........ ", username);
			logger.debug("Done Loading .................... ");
			logger.debug(
					"Now the properties are loaded , before fetching JDBC connection we must retrieve password from keyvault using vault name and secret ");
			/*
			// call the AzSecretService
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
			if (StringUtils.isNotBlank(driver) && StringUtils.isNotBlank(jdbc) && StringUtils.isNotBlank(host) && StringUtils.isNotBlank(port) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
				propsT1LoadFlag = true;
			} else {
				propsT1LoadFlag = false;
			}
		} catch (Exception ex) {
			propsT1LoadFlag = false;
			logger.debug("Unknown Exception");
			logger.debug("Error : " + ex.getMessage());
		}
		finally {
			/*
			 * if (null != reader) { try { reader.close(); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } }
			 */
		}
		return propsT1LoadFlag;
	}

	// type-1 - loads the connection viz props file from resources
	public static synchronized Connection getCurConnection() {
		if(getJdbcProperties()) {
			String uri = jdbc + "://" + host + ":" + port + "/" + sid + "?ssl=true&sslmode=require";
			logger.debug("Database URL : " + uri);
			DataSource dataSource = new DataSource();
			Connection connection = null;
			try {
				PoolProperties properties = new PoolProperties();
				properties.setDriverClassName(driver);
				properties.setUrl(uri);
				properties.setUsername(username);
				properties.setPassword(password);
				properties.setMaxWait(0);
				properties.setMinIdle(5);
				properties.setMaxIdle(10);
				dataSource.setPoolProperties(properties);
				connection = dataSource.getConnection();
				return connection;
	
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
