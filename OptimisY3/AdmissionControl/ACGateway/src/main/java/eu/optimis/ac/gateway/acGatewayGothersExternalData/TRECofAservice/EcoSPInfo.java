/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAservice;

import eu.optimis.ac.gateway.init_finish.Initialize;
import org.apache.log4j.Logger;

public class EcoSPInfo {

    public String EcoSP = "";
    public String finalMessage = "s ";
    private Logger log;
    private int status = -1;
    private int StartCounter = 0;
    
    public EcoSPInfo(Initialize initialize, String serviceManifest, String host, String port, Logger the_log) {
        log = the_log;

        log.info("EcoSP info : " + host + " " + port);
        
        if(initialize.skipTRECLevel>0)
            StartCounter = initialize.skipTRECLevel;
        else if(initialize.skipECOLevel>0)
            StartCounter = initialize.skipECOLevel;
        
        for (int i = StartCounter; i < 3; i++) {
            
            if (i == 0) {
                getEcoSPInfoFromTRECcommon(host,port,serviceManifest);
                if(status == 200) break;
            } else if (i == 1) {
                getEcoSPInfoFromTRECclient(host,port,serviceManifest);
                if(status == 200) break;
            } else {
                getEcoSPInfoFromFile();
                break;
            }
            
        }//for-i
        
        if((EcoSP.length()==3)&&(EcoSP.contains("0.0")))
        {
                log.info("Eco Error Value : "+EcoSP);
                String ecoSPvalue = EcoSP;
                getEcoSPInfoFromFile();
                finalMessage = "s "+ecoSPvalue+" ";
        }//if EcoSP="0.0"
        
        log.info("Eco Value : "+EcoSP);
    }//constructor
    
    private void getEcoSPInfoFromTRECcommon(String host,String port,String serviceManifest) {
        
        log.info("EcoSP Info From TRECcommon");
        
        String url_String = "/AC_TRECcommon_aaS/TRECcommon/getEcoSP";
        
        TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
        status = trecInfo.status;
        
        if(status == 200)
        {
            EcoSP = trecInfo.value;
            
            EcoSP = getEcoSPWith2Digits(EcoSP);
            
        }//if(status == 200)
        
        finalMessage = "sC ";

    }//getEcoSPInfoFromTRECcommon()

    private void getEcoSPInfoFromTRECclient(String host,String port,String serviceManifest) {
        
        log.info("EcoSP Info From EcoSPClient");
        
        String url_String = "/AC_TRECcommon_aaS/TRECclients/getEcoSP";
        
        TRECInfo trecInfo = new TRECInfo(host,port,serviceManifest,url_String,log);
        
        status = trecInfo.status;
        
        if(status == 200)
        {
            EcoSP = trecInfo.value;
            
            EcoSP = getEcoSPWith2Digits(EcoSP);
            
        }//if(status == 200)
        
        finalMessage = "sL ";
        
        if(StartCounter!=0)
                finalMessage = "s("+StartCounter+")L ";
            else    
                finalMessage = "sL ";
    }//getEcoSPInfoFromTRECclient()

    private void getEcoSPInfoFromFile() {
        log.info("EcoSP Info From file");

        EcoSP = "8";
        
        if(StartCounter!=0)
                finalMessage = "s("+StartCounter+")F ";
            else    
                finalMessage = "sF ";
    }//getEcoSPInfoFromFile()

    private String getEcoSPWith2Digits(String EcoSP)
    {
        
        if(!EcoSP.contains("."))
            return EcoSP;
        
        String temp[] = EcoSP.replace(".", " ").split(" ");
        
        if(temp[1].length()==1)
            return EcoSP;
        
        return temp[0]+"."+temp[1].substring(0, 2);
        
    }//getEcoSPWith2Digits()
}//class
