package eu.optimis.cbr.rest.utils;

import javax.servlet.ServletContext;


public class MyServletContext {     
	private static ServletContext context;

	private MyServletContext() {
		
	}      

	public static ServletContext getContext() {
		return context;     
	}      

	public static void setContext(ServletContext ctext) {
		context = ctext;     
	}
	
} 
