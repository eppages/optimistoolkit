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
package eu.optimis._do.schemas;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import eu.optimis.ipdiscovery.datamodel.Provider;


/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * A PlacementSolution object contains a list of Placement object,
 * which describes where components (split from an original manifest) should go.
 */

@XmlRootElement
public class PlacementSolution
{
	private boolean feasible = true;
	private List<Placement> placementList;
	private double optimum;

	public PlacementSolution()
	{
	}
	
	public boolean isFeasible()
	{
		return feasible;
	}

	public double getOptimum() throws Exception
	{
		if(this.isFeasible())
			return optimum;
		else
		{
			throw new Exception("Solution is not feasible!");
		}
	}

	public void setOptimum(double optimum)
	{
		this.optimum = optimum;
	}

	public void setFeasible(boolean feasible)
	{
		this.feasible = feasible;
	}

	public List<Placement> getPlacementList()
	{
		return placementList;
	}

	public void setPlacementList(List<Placement> placementList)
	{
		this.placementList = placementList;
	}

	@Override
	public String toString()
	{
		String result ="";
		if(this.feasible == false)
		{
			result = "Placement Solution: Not Feasible!";
		}
		else
		{
			result = "Placement Solution:  Feasible!\n";
			result +="Placement List Size = " + placementList.size()+"\n";
			for (int i = 0; i < placementList.size(); i++)
			{
				Provider provider = placementList.get(i).getProvider();
				result+=""+i+"-->"+provider.getIdentifier()+"\n";
			}
		}
		return result;
	}
	
}
