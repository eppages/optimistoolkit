/*
Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis._do.iface;

import eu.optimis._do.schemas.PlacementRequest;
import eu.optimis._do.schemas.PlacementSolution;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public interface IDO
{
	//Execution..
	public PlacementSolution getPlacementSolution(PlacementRequest request);
}
