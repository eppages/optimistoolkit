/*
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

package eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator;

import javax.persistence.*;


@Entity
@Table(name="PROVIDERS")
public class PersistentSLAProviders {

		@Id @GeneratedValue
		@Column(name="provider_id")
		private int provider_id;

		@Lob
		@Column(name="provider_epr")
		private String provider_epr;

		@Lob
		@Column(name="provider_dn")
		private String provider_dn;

		@Lob
		@Column(name="delegation_epr")
		private String delegation_epr;

		@Column(name="inService")
		private boolean inService;

		public int getProvider_id() {
			return provider_id;
		}

		public void setProvider_id(int provider_id) {
			this.provider_id = provider_id;
		}

		public String getProvider_epr() {
			return provider_epr;
		}

		public void setProvider_epr(String provider_epr) {
			this.provider_epr = provider_epr;
		}

		public String getProvider_dn() {
			return provider_dn;
		}

		public void setProvider_dn(String provider_dn) {
			this.provider_dn = provider_dn;
		}

		public String getDelegation_epr() {
			return delegation_epr;
		}

		public void setDelegation_epr(String delegation_epr) {
			this.delegation_epr = delegation_epr;
		}

		public boolean isInService() {
			return inService;
		}

		public void setInService(boolean inService) {
			this.inService = inService;
		}



	}