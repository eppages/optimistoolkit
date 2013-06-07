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
package eu.optimis._do.utils;
import java.util.ArrayList;
import java.util.List;

import eu.optimis._do.schemas.internal.Pair;

/**
 * @author Wubin.Li (Viali)
 * @author Petter Svärd
 *
 */

public class PartitionUtil<T>
{
	/**
	 * Partition a oSet into maxiSize subsets.
	 * @param oSet
	 * @param maxSize
	 * @return the partition results.
	 */
	/*
	public List<List<List<T>>> genPartitions(List<T> oSet, int maxSize)
	{
		Map<Pair<T, T>, Boolean> antiAffinities = new HashMap<Pair<T, T>, Boolean>();
		return this.genPartitions(oSet, antiAffinities, maxSize);
	}
	*/
	
	/**
	 * Partition a oSet into maxiSize subsets.
	 * @param oSet
	 * @param maxSize
	 * @return the partition results.
	 */
	public List<List<List<T>>> genPartitions(List<T> oSet,
			List<Pair<T, T>> antiAffinityPairs, int maxSize)
	{
		List<List<List<T>>> resultSet = new ArrayList<List<List<T>>>();
		List<List<T>> onePartition = new ArrayList<List<T>>();
		ArrayList<T> tempSet = new ArrayList<T>();
		List<List<List<T>>> tempResult = new ArrayList<List<List<T>>>();
		tempSet.add(oSet.get(0));
		onePartition.add(tempSet);
		tempResult.add(onePartition);
		for (int i = 1; i < oSet.size(); i++)
		{
			ArrayList<T> tempSet1 = new ArrayList<T>();
			tempSet1.add(oSet.get(i));
			for (int j = 0; j < tempResult.size(); j++)
			{
				onePartition = tempResult.get(j);
				List<List<T>> aNewPartition = new ArrayList<List<T>>();
				//Add the new element as a new subset..
				aNewPartition.add(tempSet1);
				for (int k = 0; k < onePartition.size(); k++)
				{
					aNewPartition.add(onePartition.get(k));
				}
				if (aNewPartition.size() <= maxSize)
					resultSet.add(aNewPartition);
				
				//Add the new element to each set of partitions
				for (int m = 0; m < onePartition.size(); m++)
				{
					List<List<T>> bNewPartition = new ArrayList<List<T>>();
					for (int n = 0; n < onePartition.size(); n++)
					{
						bNewPartition.add(onePartition.get(n));
					}

					List<T> tempSet3 = new ArrayList<T>();
					List<T> tempSet2 = new ArrayList<T>();
					tempSet3 = bNewPartition.get(m);
					for (int p = 0; p < tempSet3.size(); p++)
					{
						tempSet2.add(tempSet3.get(p));
					}
					
					// IMPORTANT
					T element = oSet.get(i);
					if (antiAffinityPairs == null || antiAffinityPairs.size() == 0)
					{
						tempSet2.add(element);
						bNewPartition.set(m, tempSet2);
						if (bNewPartition.size() <= maxSize)
							resultSet.add(bNewPartition);
					}
					else// Check Anti-Affinity Here..
					{
						boolean should_add = true;
						for (int t = 0; t < tempSet2.size(); t++)
						{
							T a = tempSet2.get(t);
							Pair<T, T> p = new Pair<T, T>(a, element);
							if (antiAffinityPairs.contains(p))
							{
								should_add = false;
								break;
							}
						}
						if (should_add)
						{
							tempSet2.add(element);
							bNewPartition.set(m, tempSet2);
							if (bNewPartition.size() <= maxSize)
								resultSet.add(bNewPartition);	
						}
					}

				}

			}
			List<List<List<T>>> result = new ArrayList<List<List<T>>>();
			for (int q = 0; q < resultSet.size(); q++)
			{
				result.add(resultSet.get(q));
			}
			tempResult = result;
			resultSet.clear();
		}
		resultSet = tempResult;
		return resultSet;
	}
	
	public static void main(String[] args)
	{
		ArrayList<String> oSet = new ArrayList<String>();
		/*
		for (int i = 0; i < 5; i++)
		{
			oSet.add(String.valueOf(i));
		}
		*/
		oSet.add("AC");
		oSet.add("B");
		oSet.add("DF");
		oSet.add("E");
		oSet.add("G");
		
		
		
		List<Pair<String, String>>  antiAffinities = new ArrayList<Pair<String, String>>();
		Pair<String, String> anti1 = new Pair<String, String>("E","AC");
		Pair<String, String> anti2 = new Pair<String, String>("B","DF");
		Pair<String, String> anti3 = new Pair<String, String>("E","DF");
		antiAffinities.add(anti1);
		antiAffinities.add(anti2);
		antiAffinities.add(anti3);
		
		PartitionUtil<String> pObject = new PartitionUtil<String>();
		List<List<List<String>>> result = pObject.genPartitions(oSet, antiAffinities, 3);
		for (int i = 0; i < result.size(); i++)
		{
			System.out.println(result.get(i));
		}
		System.out.println("\n The number of different partitions: "
				+ result.size() + " !");
	}	
}
