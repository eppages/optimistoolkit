package eu.optimis.sla;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.ogf.schemas.graap.wsAgreement.AgreementType;

import eu.optimis.types.servicemanifest.DataProtectionSectionType;
import eu.optimis.types.servicemanifest.ElasticityArraySectionType;
import eu.optimis.types.servicemanifest.ServiceManifestDocument;
import eu.optimis.types.servicemanifest.TRECSectionType;
import eu.optimis.types.servicemanifest.VirtualMachineDescriptionDocument;
import eu.optimis.types.servicemanifest.VirtualMachineDescriptionType;

public class Tools {
	
    private static final Logger log = Logger.getLogger(Tools.class);
    
	private static final String opt = "declare namespace opt ='http://schemas.optimis.eu/optimis/';"; 
	
	private static String vmdPath = opt + "//opt:VirtualMachineDescription";
	private static String trecPath = opt + "//opt:TRECSection";
	private static String elasticityPath = opt + "//opt:ElasticitySection";
	private static String dataProtectionPath = opt + "//opt:DataProtectionSection";
	
	public static ServiceManifestDocument offerToServiceManifest(AgreementType offer) {
	    log.info("initialize service manifest");
	    
		ServiceManifestDocument manifest = ServiceManifestDocument.Factory.newInstance();
        manifest.addNewServiceManifest();
		
		try {
		    XmlObject initiator = offer.getContext().getAgreementInitiator();
		    String initiatorId = XmlString.Factory.parse(initiator.getDomNode()).getStringValue();
		    manifest.getServiceManifest().setServiceProviderId(initiatorId);
		}
		catch (Exception e) {
		    //
		    // TODO: should throw exception
		    //
		    log.error("error extractin SP id, use default");
            manifest.getServiceManifest().setServiceProviderId("OPTIMUMWEB");
		}
		
        log.info("convert sla offer");
		VirtualMachineDescriptionType vmd = (VirtualMachineDescriptionType) offer.selectPath(vmdPath)[0];
		TRECSectionType trec = (TRECSectionType) offer.selectPath(trecPath)[0];
		ElasticityArraySectionType elasicity = (ElasticityArraySectionType) offer.selectPath(elasticityPath)[0];
		DataProtectionSectionType dpa = (DataProtectionSectionType) offer.selectPath(dataProtectionPath)[0];
		
		manifest.getServiceManifest().setManifestId(offer.getContext().getTemplateName() + ":" + offer.getContext().getTemplateId());
		manifest.getServiceManifest().setServiceDescriptionSection(vmd);
		//
		// substitute the ServiceDescriptionSection with VirtualMachineDescription
		//
		manifest.getServiceManifest().getServiceDescriptionSection().substitute(VirtualMachineDescriptionDocument.type.getDocumentElementName(), VirtualMachineDescriptionType.type);		
		manifest.getServiceManifest().setElasticitySection(elasicity);
		manifest.getServiceManifest().setTRECSection(trec);
		manifest.getServiceManifest().setDataProtectionSection(dpa);

		log.info("sla offer convertion completed");
		
		return manifest;
	}

}
