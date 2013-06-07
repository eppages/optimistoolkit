/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
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

package es.bsc.servicess.ide.editors.deployers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ISOCountryCodeMap {
	public static final HashMap<String, String> codeToCountries;
	static{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("AD", "Andorra"); map.put("AR", "Argentina"); map.put("AT", "Austria");
		map.put("BE", "Belgium"); map.put("BG", "Bulgaria"); map.put("CA", "Canada");
		map.put("CY", "Cyprus"); map.put("CZ", "Czech Republic"); map.put("DK", "Denmark");
		map.put("EE", "Estonia"); map.put("FI", "Finland"); map.put("FR", "France");
		map.put("FO", "Faroe Islands"); map.put("DE", "Germany"); map.put("GR", "Greece");
		map.put("GG", "Guernsey"); map.put("HU", "Hungary"); map.put("IE", "Ireland");
		map.put("IM", "Isle of Man"); map.put("IL", "Israel"); map.put("IT", "Italy");
		map.put("JE", "Jersey"); map.put("LT", "Lithuania"); map.put("LU", "Luxembourg");
		map.put("LV", "Latvia"); map.put("MT", "Malta"); map.put("NL", "Netherlands"); 
		map.put("PL", "Poland"); map.put("NZ", "New Zealand");
		map.put("PT", "Portugal"); map.put("RO", "Romania"); map.put("SK", "Slovakia");
		map.put("SI", "Slovenia"); map.put("ES", "Spain"); map.put("SE", "Sweden");
		map.put("CH", "Switzerland"); map.put("US", "United States");map.put("UY", "Uruguay"); map.put("GB", "United Kingdom");
		codeToCountries = map;
	}	
	public static String[] whiteListContryCodes = new String[]{
		"BE","FI","LU","NL","DE","IT","GB","IE","DK",
		"GR","PT","ES","SE","BG","FR","MT","HU","EE",
		"CZ","LV","PL","CY","LT","SI","SK","AT","RO",
		"FO","IL","AD","CA","AR","CH","GG","JE","IM",
		"US","UY","NZ"};  
    
	public static String[] getAllWhiteList(){
		String[] countries = codeToCountries.values().toArray(new String[codeToCountries.size()]);
		Arrays.sort(countries);
		return countries;
	}
	
	public static String getCountryName(String code){
		return codeToCountries.get(code);
	}
	
	public static String getCountryCode(String name){
		for (Map.Entry<String, String> codeEntry: codeToCountries.entrySet()){
			if (codeEntry.getValue().equalsIgnoreCase(name)){
				return codeEntry.getKey();
			}
		}
		return null;
	}

	public static String[] getCountryNames(String[] countryCodes) {
		if (countryCodes!=null&& countryCodes.length>0){
			String[] countryNames = new String[countryCodes.length];
			for (int i=0; i<countryCodes.length; i++){
				countryNames[i]= codeToCountries.get(countryCodes[i]);
			}
			return countryNames;
		}else
			return new String[0];
	}
    
}
