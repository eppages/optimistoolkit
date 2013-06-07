package eu.optimis.manifest.api.test;

import java.io.InputStream;
import java.util.Properties;

import org.apache.xmlbeans.XmlError;

import eu.optimis.manifest.api.sp.EcoEfficiencySection;
import eu.optimis.manifest.api.sp.EcoMetric;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanBREEAMCertificationConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanCASBEEType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanGreenStarType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanISO14000Type;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanLEEDCertificationConstraintType;


public class EcoEfficiencyTest extends AbstractTestApi 
{
	public void testCreateManifest() throws Exception
    {

        Properties properties = new Properties();
        InputStream in = this.getClass().getResourceAsStream( "/dummy.manifest.properties" );
        properties.load( in );
        
        Manifest manifest = Manifest.Factory.newInstance( "DummyApp", "dummyComponent", properties );
        
        System.out.println(manifest.getTRECSection().getEcoEfficiencySectionArray(0).toString());
        
        // add another eco efficiency section for mysql and jboss component
        EcoEfficiencySection eco = manifest.getTRECSection().addNewEcoEfficiencySection( "dummyComponent" );
        eco.setLEEDCertification( XmlBeanLEEDCertificationConstraintType.GOLD.toString() );
        eco.setBREEAMCertification(XmlBeanBREEAMCertificationConstraintType.GOOD.toString());
        eco.setEuCoCCompliant( true );
        eco.setEnergyStarRating("10");
        eco.setISO14000(XmlBeanISO14000Type.ISO_14001_COMPLIANT.toString());
        eco.setGreenStar(XmlBeanGreenStarType.X_4.toString());
        eco.setCASBEE(XmlBeanCASBEEType.B_2.toString());
        
        EcoMetric metric1 = manifest.getTRECSection().getEcoEfficiencySectionArray(1).addNewEcoMetric("EnergyEfficiency");
        metric1.setSLAType("Soft");
        metric1.setThresholdValue("NotSpecified");
        metric1.setMagnitudePenalty("NA");
        metric1.setTimePenalty("NA");
        
        EcoMetric metric2 = manifest.getTRECSection().getEcoEfficiencySectionArray(1).addNewEcoMetric("EcologicalEfficiency");
        metric2.setSLAType("Hard");
        metric2.setThresholdValue("85.6");
        metric2.setMagnitudePenalty("0.6");
        metric2.setTimePenalty("0.8");
        
        System.out.println(manifest.getTRECSection().getEcoEfficiencySectionArray(1).toString());
        
        
        
        if ( manifest.hasErrors() )
        {
            for ( XmlError error : manifest.getErrors() )
            {
                System.out.println( error );
            }
        }
    }

}
