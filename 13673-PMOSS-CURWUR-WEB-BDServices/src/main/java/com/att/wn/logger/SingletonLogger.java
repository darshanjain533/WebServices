package com.att.wn.logger;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SingletonLogger {
	public static Logger getInstance(){
	  	Logger logger = null;
	 	if(logger == null)
    	logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);	
	 	// this will come from app service 
	 	String logLevelFromEnv=System.getenv("WUR_LOG_LEVEL");
	 	if (StringUtils.isNotBlank(logLevelFromEnv)){
	 		if(logger.getLevel().toString().toUpperCase() .equalsIgnoreCase(logLevelFromEnv)) {
	 			//logger.info("No change in log level needed as configured one matches with environment property setting");
		} else {
			switch (logLevelFromEnv) {
			case "OFF":
				logger.setLevel(Level.OFF);
				break;
			case "ALL":
				logger.setLevel(Level.ALL);
				break;
			case "ERROR":
				logger.setLevel(Level.ERROR);
				break;
			case "WARN":
				logger.setLevel(Level.WARN);
				break;
			case "TRACE":
				logger.setLevel(Level.TRACE);
				break;
			case "DEBUG":
				logger.setLevel(Level.DEBUG);
				break;
			case "INFO":
				logger.setLevel(Level.INFO);
				break;
			default:
				logger.setLevel(Level.ALL);
			}
		}
	 	}else {
	 			//logger.info("No explicit logger level change configured.. hence using the default level => {} ", logger.getLevel().toString().toUpperCase());
	 	}		 	
	 	 return logger;	 
	}
}
