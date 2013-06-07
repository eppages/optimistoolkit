package eu.optimis.elasticityengine;

import org.apache.log4j.Logger;

import eu.optimis.elasticityengine.manifest.Rule;

/**
 * Represents a single elasticity rule parsed from the manifest.
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
public class ElasticityRule {

    protected final static Logger log = Logger.getLogger(ElasticityRule.class);

    private String imageID; // "VirtualSystemID"
    private String kpiName;
    private String window;
    private int frequency;
    private int quota;
    private float tolerance;

    public ElasticityRule(String imageID, Rule rule) {
        this(imageID, rule.kpiName, rule.window, rule.frequency, rule.quota, rule.tolerance);
    }

    public ElasticityRule(String imageID, String kpiName, String window, int frequency, int quota,
            float tolerance) {
        this.imageID = imageID;
        this.kpiName = kpiName;
        this.window = window;
        this.frequency = frequency;
        this.quota = quota;
        this.tolerance = tolerance;
    }

    public String getImageID() {
        return imageID;
    }

    public String getWindow() {
        return window;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getQuota() {
        return quota;
    }

    public float getTolerance() {
        return tolerance;
    }

    public String getKPIName() {
        return kpiName;
    }

    @Override
    public String toString() {
        return "ElasticityRule [imageID=" + imageID + ", kpiName=" + kpiName + ", window=" + window
                + ", frequency=" + frequency + ", quota=" + quota + ", tolerance=" + tolerance + "]";
    }

}
