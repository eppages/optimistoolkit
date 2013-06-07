package eu.optimis.cbr.rest.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

public class OutputStub4Demo
{
	private static Logger logger = Logger.getLogger(OutputStub4Demo.class);

	private static String defaultPath = "/var/lib/tomcat6/logs/broker.out";

	public static void setFileOutputPath(String filePath)
	{
		OutputStub4Demo.defaultPath = filePath;
	}

	public static void write(String message)
	{
		try
		{
			File f = new File(OutputStub4Demo.defaultPath);
			if (f.exists() == false)
			{
				if (f.createNewFile() == false)
				{
					logger.error("Can not create new file: "
							+ OutputStub4Demo.defaultPath);
					return;
				}
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f, true)));
			out.write(message);
			out.close();
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			logger.error("Failed!! " + e.getMessage());
		}
	}
}