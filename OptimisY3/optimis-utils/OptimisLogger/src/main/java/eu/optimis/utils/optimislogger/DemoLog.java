package eu.optimis.utils.optimislogger;

import org.apache.log4j.Level;
//import org.apache.log4j.Priority;

public class DemoLog extends Level {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = -1238611148227415393L;

	public DemoLog(int level, String levelStr, int syslogEquivalent)
	    {
	        super(level, levelStr, syslogEquivalent);
	    }

	    public static DemoLog toLevel(int val, Level defaultLevel)
	    {
	         return DEMO;
	    }

	    public static DemoLog toLevel(String sArg, Level defaultLevel)
	    {   
	         return DEMO;     
	    }

	    public static final DemoLog DEMO = new DemoLog(60000, "DEMO", 0);

}
