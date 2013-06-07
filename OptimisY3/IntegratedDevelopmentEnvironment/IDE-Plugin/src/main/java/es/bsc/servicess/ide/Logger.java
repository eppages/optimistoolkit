/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package es.bsc.servicess.ide;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

/**
 * Class for integrating eclipse logging with apache log4j interface
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class Logger {
	private Class className;
	private ILog log;
	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String WARN = "WARNING";
	public static final String ERROR = "ERROR";
	private static final int DEBUG_LEVEL = 3;
	private static final int INFO_LEVEL = 2;
	private static final int WARN_LEVEL = 1;
	private static final int ERROR_LEVEL = 0;
	
	private int level;
	
	/**
	 * Logger constructor
	 * @param className
	 */
	public Logger(Class className) {
		this.className = className;
		this.log = Activator.getDefault().getLog();
		try{
			String strLevel = new IDEProperties(System.getenv("HOME")+"/.ide/config.properties").getLogLevel();
			if (strLevel.equalsIgnoreCase(DEBUG)){
				this.level = DEBUG_LEVEL;
				System.out.println("Log level set to DEBUG");
			}else if (strLevel.equalsIgnoreCase(INFO)){
				this.level = INFO_LEVEL;
				System.out.println("Log level set to INFO");
			} else if (strLevel.equalsIgnoreCase(WARN)){
				this.level = WARN_LEVEL;
				System.out.println("Log level set to WARN");
			} else if (strLevel.equalsIgnoreCase(ERROR)){
				this.level = ERROR_LEVEL;
				System.out.println("Log level set to ERROR");
			}
		}catch(Exception e){
			this.level = WARN_LEVEL;
			System.out.println("Log level set to WARN");
		}
	}
	
	/**
	 * Factory method to get a logger without className
	 * @return Logger
	 */
	public static Logger getLogger(){
		return new Logger(null);
	}
	
	/**Factory method to get a logger with className
	 * @param className Class name
	 * @return Logger
	 */
	public static Logger getLogger(Class className){
		return new Logger(className);	
	}
	
	/**
	 * Write INFO type logs
	 * @param message Message
	 */
	public void info(String message){
		if (level>=INFO_LEVEL){
			StackTraceElement invoker = new Throwable().fillInStackTrace().getStackTrace()[1];
			log.log(new Status(Status.OK, Activator.PLUGIN_ID, printInvocationInfo(invoker) + " " + message));
		}
	}
	
	/**
	 * Write DEBUG type logs
	 * @param message Message
	 */
	public void debug(String message){
		if (level>=DEBUG_LEVEL){
			StackTraceElement invoker = new Throwable().fillInStackTrace().getStackTrace()[1];
			log.log(new Status(Status.INFO, Activator.PLUGIN_ID, printInvocationInfo(invoker)+" "+ message));
		}
	}
	
	/**
	 * Write WARNING type logs
	 * @param message Message
	 */
	public void warn(String message){
		if (level>=WARN_LEVEL){
			StackTraceElement invoker = new Throwable().fillInStackTrace().getStackTrace()[1];
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID, printInvocationInfo(invoker)+" "+ message));
		}
	}
	
	/**
	 * Write ERROR type logs
	 * @param message Message
	 */
	public void error(String message){
		if (level>=ERROR_LEVEL){
			StackTraceElement invoker = new Throwable().fillInStackTrace().getStackTrace()[1];
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, printInvocationInfo(invoker)+" "+ message));
		}
	}
	
	public void error(String message, Throwable e){
		if (level>=ERROR_LEVEL){
			StackTraceElement invoker = new Throwable().fillInStackTrace().getStackTrace()[1];
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, printInvocationInfo(invoker)+" "+ message));
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, printStackTrace(e)));
		}
	}
	
	private String printInvocationInfo(StackTraceElement invoker){
		return "["+invoker.getClassName()+"."+invoker.getMethodName()+"("+invoker.getFileName()+":"+invoker.getLineNumber()+")]";
	}
	
	private String printStackTrace(Throwable e){
		StringWriter strWtr = new StringWriter();
		PrintWriter ptrWritter = new PrintWriter(strWtr);
		e.printStackTrace(ptrWritter);
		return strWtr.toString();
	}
	
}
