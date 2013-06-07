/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acLogsToWeb;


import eu.optimis.ac.gateway.configuration.GetFileNames;
import eu.optimis.ac.gateway.utils.AddOptimisPhoto;
import eu.optimis.ac.gateway.utils.FileFunctions;

public class GetAClogs
{

	public static String getHTML(Boolean withLogoPhoto,Boolean autorefresh)
	{                
		String msg = FileFunctions.readFileAsStringWithPath(GetFileNames.getAClogs_fileName());
                
		String logsMessage="";
		
		logsMessage+="<html>";
		
		logsMessage+="<head>";
		logsMessage+="<title>AClogs</title>";
                
                if(autorefresh)
                {
                    logsMessage+="<script type=\"text/JavaScript\">";
                    logsMessage+="function timedRefresh(timeoutPeriod) {"
                        + "setTimeout(\"location.reload(true);\",timeoutPeriod);"
                        +"}";
                    logsMessage+="</script>";
                }
                
		logsMessage+="</head>";
                
                if(autorefresh)
                    logsMessage+="<body  onload=\"JavaScript:timedRefresh(30000);\">";
                else
                    logsMessage+="<body>";
                
                if(withLogoPhoto)
                    logsMessage+=AddOptimisPhoto.addPhoto().replace('^','"');
                
		logsMessage+=msg;
		logsMessage+="</body>";
		logsMessage+="</html>";
                
		return logsMessage;
		
	}//getHTML(Logger log)
	
	
	//delete this method
	public static String getHTMLpage()
	{
		
		
		String logsMessage="<html>";
		logsMessage+="<head>";
		logsMessage+="<title>AClogs</title>";
		logsMessage+="<script type=^text/javascript^> ";
		logsMessage+="function Read() {";
		logsMessage+=" var Scr = new ActiveXObject(^Scripting.FileSystemObject^);";
		logsMessage+=" var CTF = Scr.OpenTextFile(^";
		logsMessage+=GetFileNames.getAClogs_fileName();
		logsMessage+="^, 1, true);";
		logsMessage+=" var data = CTF.ReadAll();";
		logsMessage+=" alert(data);";
		logsMessage+=" CTF.Close(); } ";
		logsMessage+="";
		logsMessage+="";
		logsMessage+="";
		logsMessage+="";
		logsMessage+="";
		
		logsMessage+="</script>";
		logsMessage+="</head>";
		logsMessage+="<body onLoad=^Read()^>";
		logsMessage+=AddOptimisPhoto.addPhoto();
		logsMessage+="";
		logsMessage+="";
		logsMessage+="";
		logsMessage+="";
		logsMessage+="</body>";
		logsMessage+="</html>";
		
		return logsMessage.replace('^','"');
	}//getHTMLpage(Logger log)
}//class