package eu.optimis.elasticityengine.sc;

import eu.optimis.elasticityengine.ElasticityCallback;

/**
 * Interface for service controllers. Each service in the system has a specific controller where elasticity for that service is handled.
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
public interface ServiceController {

    public abstract void setCallback(ElasticityCallback callback);

    public abstract void unSetCallback();

    public abstract int getCurrentPrediction(String imageID, int timeSpanInMinutes);

    public abstract void setManifest(String serviceManifest);

    public abstract ElasticityCallback getCallback();

    public abstract String getServiceManifest();

	void setMode(boolean Proactive);

	public abstract void destroy();

}