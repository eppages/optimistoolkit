/**
 *  Copyright 2013 University of Leeds
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
package eu.optimis.mi.gui.client.model.riskdao;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author scsmk
 */

@Entity
@Table(name="ip_risk")
public class IpRiskObject implements Cloneable{
      public Object clone() 
        {
		
		try 
                {
                    return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		
		
	}
        
        @Id
	@Column(name="ip_id")
	private String ip_id;
	
	@Column(name="ip_name")
	private String ip_name;
	
        @Column(name="vmFailure_risk")
	private double vmFailure_risk;
	
        @Column(name="security_risk")
	private double security_risk;
	
        @Column(name="legal_risk")
	private double legal_risk;
	
        @Column(name="datamanagement_risk")
	private double datamanagement_risk;
	
    
        public String ip_id()
        {
            return ip_id;
        }
    
        public void set_ip_id(String aip_id)
        {
            this.ip_id=aip_id;
        }    
        
        public String get_ip_name()
        {
            return ip_name;
        }

        public void set_ip_name(String aip_name)
        {
            this.ip_name= aip_name;
        }
    
        public double get_vmFailure_risk()
        {
            return vmFailure_risk;
        }

        public void set_vmFailure_risk(double avmFailure_risk)
        {
            this.vmFailure_risk= avmFailure_risk;
        }
    
          public double get_security_risk()
        {
            return security_risk;
        }

        public void set_security_risk(double asecurity_risk)
        {
            this.security_risk= asecurity_risk;
        }
        
          public double get_legal_risk()
        {
            return legal_risk;
        }

        public void set_legal_risk(double alegal_risk)
        {
            this.legal_risk= alegal_risk;
        }
        
          public double get_datamanagement_risk()
        {
            return datamanagement_risk;
        }

        public void set_datamanagement_risk(double adatamanagement_risk)
        {
            this.datamanagement_risk= adatamanagement_risk;
        }
}
