/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.trust;

import java.util.Random;

import org.jboss.logging.Logger;

import eu.optimis.manifest.api.ip.DataProtectionSection;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.tf.ip.service.operators.Opinion;
import eu.optimis.tf.ip.service.utils.GetIPManifestValues;

public class SPLegalAspects {

	Logger log = Logger.getLogger(this.getClass().getName());

	// private DataProtectionSection dps = null;
	private final static int MAX_LEGAL = 5;

	public SPLegalAspects() {
		log.info("Calculating legal aspects");

		// dps = gipmv.getDataProtectionSection();
	}

	public double calculateLegalAspects(String serviceId) 
	{
		GetIPManifestValues gipmv = new GetIPManifestValues();
		String manifest = gipmv.getServiceManifest(serviceId);		
		Manifest mani = gipmv.stringManifest2Manifest(manifest);
		log.info("Manifest imported correctly");
		
		DataProtectionSection dps = mani.getDataProtectionSection();
		int positivedataprotection = 0;
		int positiveelegiblecountrylist = 0;
		int possitiveencryptionalgorithm = 0;
		int positiveencryptionkeysize = 0;
		int positivenonelegiblecountrylist = 0;

		int positive = 0;
		try 
		{
			if (dps.getDataProtectionLevel() != null) 
			{
				positive += getPositiveDataProtection(dps
						.getDataProtectionLevel());
//				positivedataprotection = getPositiveDataProtection(dps
//						.getDataProtectionLevel());
//				log.info("positivedataprotection " + positivedataprotection);
			}
		} 
		catch (Exception e) 
		{
			log.info(e.getMessage());			
		}
		try {
			if (dps.getEligibleCountryList() != null) {
				positive += getPositiveElegibleCountryList(dps
						.getEligibleCountryList());
//				positiveelegiblecountrylist = getPositiveElegibleCountryList(dps
//						.getEligibleCountryList());
//				log.info("positiveelegiblecountrylist: "
//						+ positiveelegiblecountrylist);
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			positive += 0;
		}
		try {
			if (dps.getDataEncryptionLevel() != null) {
				try {
					if (dps.getDataEncryptionLevel().getEncryptionAlgorithm() != null) {
						positive += getPositiveEncryptionAlgorithm(dps
								.getDataEncryptionLevel()
								.getEncryptionAlgorithm());
//						possitiveencryptionalgorithm = getPositiveEncryptionAlgorithm(dps
//								.getDataEncryptionLevel()
//								.getEncryptionAlgorithm());
//						log.info("possitiveencryptionalgorithm: "
//								+ possitiveencryptionalgorithm);
					}
				} catch (Exception e) {
					log.info(e.getMessage());
					positive += 0;
				}
				try {
					if (dps.getDataEncryptionLevel().getEncryptionKeySize() != 0) {
						positive += getPositiveEncryptionKeySize(dps
								.getDataEncryptionLevel()
								.getEncryptionKeySize());
//						positiveencryptionkeysize = getPositiveEncryptionKeySize(dps
//								.getDataEncryptionLevel()
//								.getEncryptionKeySize());
//						log.info("positiveencryptionkeysize: "
//								+ positiveencryptionkeysize);
					}

				} catch (Exception e) {
					log.info(e.getMessage());
					positive += 0;
				}
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			positive += 0;
		}
		try {
			if (dps.getNonEligibleCountryList() != null) {
				positive += getPositiveNonElegibleCountryList(dps
						.getNonEligibleCountryList());
//				positivenonelegiblecountrylist = getPositiveNonElegibleCountryList(dps
//						.getNonEligibleCountryList());
//				log.info("positivenonelegiblecountrylist: "
//						+ positivenonelegiblecountrylist);
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			positive += 0;
		}

		// int positive = positveLegal(MAX_LEGAL,3);
		int negative = MAX_LEGAL - positive;

		Opinion op = new Opinion(positive, negative);
		log.info("Legal aspects :" + op.getExpectation());
		return op.getExpectation();
	}

	private int getPositiveElegibleCountryList(String[] countryList) {
		log.info("getPositiveElegibleCountryList " + countryList.length);
		// String[] countryList = dps.getEligibleCountryList();
		if (countryList.length == 0) {
			return 0;
		} else {
			return 1;
		}

	}

	private int getPositiveNonElegibleCountryList(String[] countryList) {
		log.info("getPositiveNonElegibleCountryList " + countryList.length);
		// String[] countryList = dps.getNonEligibleCountryList();
		if (countryList.length == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	private int getPositiveEncryptionKeySize(int encryptionKeySize) {
		log.info("getPositiveEncryptionKeySize " + encryptionKeySize);
		if (encryptionKeySize < 100) {
			return 0;
		} else {
			return 1;
		}

	}

	private int getPositiveEncryptionAlgorithm(String encryptionAlgorithm) {
		log.info("getPositiveEncryptionAlgorithm " + encryptionAlgorithm);
		if (encryptionAlgorithm.equalsIgnoreCase("NotApplicable")) {
			return 0;
		} else {
			return 1;
		}

	}

	private int getPositiveDataProtection(String dpl) 
	{
		log.info("Data Protection Section --> " + dpl);
		if (dpl.equalsIgnoreCase("None")) {
			return 0;
		} else {
			return 1;
		}

	}

	private int positveLegal(int minimum, int maximum) {
		int randomNum = 0;
		Random rn = new Random();
		int n = maximum - minimum + 1;
		int i = rn.nextInt() % n;
		randomNum = minimum + i;
		return randomNum;
	}
}
