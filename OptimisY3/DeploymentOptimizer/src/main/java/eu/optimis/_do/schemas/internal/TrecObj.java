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
package eu.optimis._do.schemas.internal;

public class TrecObj
{
	private double trust;
	private double risk;
	private double eco;
	private double energy;
	private double cost;

	public TrecObj(double trust, double risk, double eco, double energy, double cost)
	{
		super();
		this.trust = trust;
		this.risk = risk;
		this.eco = eco;
		this.energy = energy;
		this.cost = cost;
	}

	public double getTrust()
	{
		return trust;
	}

	public void setTrust(double trust)
	{
		this.trust = trust;
	}

	public double getRisk()
	{
		return risk;
	}

	public void setRisk(double risk)
	{
		this.risk = risk;
	}

	public double getEco()
	{
		return eco;
	}

	public void setEco(double eco)
	{
		this.eco = eco;
	}

	public double getEnergy()
	{
		return energy;
	}

	public void setEnergy(double energy)
	{
		this.energy = energy;
	}

	public double getCost()
	{
		return cost;
	}

	public void setCost(double cost)
	{
		this.cost = cost;
	}

	@Override
	public String toString()
	{
		String result="";
		result += "T= " + this.getTrust() + 
				", R= " + this.getRisk() +
				", E= ["+ this.getEco() + "," + this.getEnergy() + "]" +
				", C= " + this.getCost();
		return result;
	}
	
}
