package eu.optimis.elasticityengine.sc;

import java.util.HashSet;
import java.util.Set;

import eu.optimis.elasticityengine.ElasticityRule;

/**
 * Image controller. Each image in a service has a specific controller with a set of rules for 
 * that image.
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
public class ImageController {

    private String imageID;
    private Set<ElasticityRule> ruleSet;
    private Integer currentAmount;

    public ImageController(String imageID, Set<ElasticityRule> ruleSet) {
        this.imageID = imageID;
        this.ruleSet = ruleSet;
    }

    public Integer getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Integer prediction) {
        this.currentAmount = prediction;
    }

    public String getImageID() {
        return imageID;
    }

    public Set<String> getKPINames() {
        Set<String> kpiNames = new HashSet<String>();

        for (ElasticityRule er : ruleSet) {
            kpiNames.add(er.getKPIName());
        }

        return kpiNames;
    }

    public Set<ElasticityRule> getRuleSet() {
        return ruleSet;
    }
}
