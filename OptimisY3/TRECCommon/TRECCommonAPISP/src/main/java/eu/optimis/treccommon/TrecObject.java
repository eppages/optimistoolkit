/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
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

package eu.optimis.treccommon;

/**
 *
 * @author mariamkiran
 */
public class TrecObject {
    
    int risk;
    double trust;
    double energyEfficiency;
    double ecologicalEfficiency;
    String cost;
    
    public TrecObject()
    {
        trust=0.0;
        risk=1;
        energyEfficiency=-1.0;
        ecologicalEfficiency=-1.0;
        cost=null;
        
    }
    
    public double get_trust()
    {
        return trust;
    }
    
    public void set_trust(double atrust)
    {
        trust=atrust;
    }
    
    
    public int get_risk()
    {
        return risk;
    }
    
      public void set_risk(int arisk)
    {
        risk=arisk;
    }
    
    public double getEnergyEfficiency()
    {
        return energyEfficiency;
    }
    
    public void setEnergyEfficiency(double energyEfficiency)
    {
        this.energyEfficiency=energyEfficiency;
    }
    
    public double getEcologicalEfficiency()
    {
        return ecologicalEfficiency;
    }
    
    public void setEcologicalEfficiency(double ecologicalEfficiency)
    {
        this.ecologicalEfficiency=ecologicalEfficiency;
    }
    
    public String get_cost()
    {
        return cost;
    }
    
     public void set_cost(String acost)
    {
        cost=acost;
    }
    
    
}
