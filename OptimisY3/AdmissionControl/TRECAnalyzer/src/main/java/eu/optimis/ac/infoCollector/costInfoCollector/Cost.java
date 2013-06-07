/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.infoCollector.costInfoCollector;

class Cost 
{
	private int basic_cost;
	private int extra_cost_per_elastic_vm;
	
	protected Cost() {
	}
	
	protected void setBasic_cost(int base_cost) {
		basic_cost = base_cost;
	}
	
	protected void setExtra_cost_per_elastic_vm(int extra) {
		extra_cost_per_elastic_vm = extra;
	}
	
	protected int getBasic_cost() {
		return basic_cost;
	}
	
	protected int getExtra_cost_per_elastic_vm() {
		return extra_cost_per_elastic_vm;
	}
}//class Cost
