/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring.storage;

import java.io.IOException;
import java.util.Set;

import eu.optimis_project.monitoring.Measurement;

public interface StorageManager {

    boolean storeData(Measurement measurement) throws IOException;

	Set<Measurement> getData(String serviceID) throws IOException;

	Set<Measurement> getAllData() throws IOException;

	int removeData(String serviceID) throws IOException;

	int removeAllData() throws IOException;

	void shutdown();

}
