/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer.compinations;

import java.util.ArrayList;

public class CompinationAll {

	public ArrayList<String> ListOfCompinations = new ArrayList<String>();
	
	public CompinationAll(int componentsLength)
	{
		int maxCompineValue = componentsLength;
		
		ArrayList<String> elements = new ArrayList<String>();
		
		for(int i=1;i<=componentsLength;i++)
		{
			elements.add("vm"+i+"-");
		}//for-i
		
		for(int i=1;i<=maxCompineValue;i++)
		{
			findCompinations(elements,i);
			
		}//for-i
		
	}//constructor
	
	private void findCompinations(ArrayList<String> elements,int compineValue)
	{
		
		int[] indices;
		CombinationGenerator x = new CombinationGenerator (elements.size(), compineValue);
		StringBuffer combination;
		while (x.hasMore ()) {
		  combination = new StringBuffer ();
		  indices = x.getNext ();
		  for (int i = 0; i < indices.length; i++) {
		    combination.append (elements.get(indices[i]));
		  }
		  System.out.println (combination.toString ());
		  
		  ListOfCompinations.add(combination.toString ());
		}
	}//findCompinations()

}//class
