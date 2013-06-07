
package eu.optimis.ac.smanalyzer.smInfo;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

class AffinityConstraints {
    
    protected static String extractAffinityConstraints(Element manifest,ServiceComponentInfo sc_info,Logger log,String Tag,Boolean DisplayAllLogs)
	{
                if(DisplayAllLogs)
		log.info("Start of extract"+Tag+"Constraints.");
		
		String AffinityConstraints = null;
			
		if(manifest.getTextContent().equals("Low"))
			AffinityConstraints = "Low";
		else if(manifest.getTextContent().equals("Medium"))
			AffinityConstraints = "Medium";
		else if(manifest.getTextContent().equals("High"))
			AffinityConstraints = "High";
		else
                {
			log.error("AffinityConstraints content :"+manifest.getTextContent());
			return "";
		}
		
                if(DisplayAllLogs)
		log.info(Tag+"Constraints : "+AffinityConstraints);
		
                if(Tag.equals("Affinity"))
                {
                    sc_info.setAffinityConstraints(AffinityConstraints);
                    if(DisplayAllLogs)
                    log.info(Tag+" "+sc_info.getAffinityConstraints());
                }
                    
                else if(Tag.equals("AntiAffinity"))
                {
                    sc_info.setAntiAffinityConstraints(AffinityConstraints);
                    if(DisplayAllLogs)
                    log.info(Tag+" "+sc_info.getAntiAffinityConstraints());
                }
                else
		{
			log.error("AffinityConstraints Tag  :"+Tag);
			return "";
		}
                
                if(DisplayAllLogs)
		log.info("End of extract"+Tag+"Constraints.");
	
                return AffinityConstraints;
	}//extractAffinityConstraints()
}//class
