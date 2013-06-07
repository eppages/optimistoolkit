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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 * 
 */

public class Pair<TypeA, TypeB>
{
	private TypeA a;
	private TypeB b;

	public Pair(TypeA a, TypeB b)
	{
		super();
		this.a = a;
		this.b = b;
	}

	public TypeA getA()
	{
		return a;
	}

	public void setA(TypeA a)
	{
		this.a = a;
	}

	public TypeB getB()
	{
		return b;
	}

	public void setB(TypeB b)
	{
		this.b = b;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj instanceof Pair)
		{
			@SuppressWarnings("unchecked")
			Pair<TypeA, TypeB> p =  (Pair<TypeA, TypeB>) obj;
			TypeA a1 = p.getA();
			TypeB b1 = p.getB();
			return (a.equals(a1) && b.equals(b1))
					|| (a.equals(b1) && b.equals(a1));

		}
		return false;
	}
	

	@Override
	public int hashCode()
	{
		String r = "";
		if (a.hashCode() > b.hashCode())
			r = a + "-" + b;
		else
			r = b + "-" + a;
		return r.hashCode();
	}

	public static void main(String args[]) throws Exception
	{
		Pair<String, String> p1 = new Pair<String, String>("a", "b");
		Pair<String, String> p2 = new Pair<String, String>("b", "a");
		List<Pair<String,String>> plist=new ArrayList<Pair<String,String>>();
		plist.add(new Pair<String, String>("a", "b"));
		
		System.out.println(p1.equals(p2));
		System.out.println(plist.contains(p1));
	}
}
