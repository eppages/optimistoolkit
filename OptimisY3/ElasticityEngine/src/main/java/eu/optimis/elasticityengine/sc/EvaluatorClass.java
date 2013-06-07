package eu.optimis.elasticityengine.sc;

/**
* 
 * @author Ahmed Ali-Eldin (<a
 *         href="mailto:ahmeda@cs.umu.se">ahmeda@cs.umu.se</a>)
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

import net.astesana.javaluator.DoubleEvaluator;
import org.apache.log4j.Logger;


public class EvaluatorClass {
	protected final static Logger log = Logger
			.getLogger(EvaluatorClass.class);
	protected static DoubleEvaluator evaluator;

	public EvaluatorClass(){
		log.info("Starting Evaluator Class");
	    this.evaluator = new DoubleEvaluator();
		log.info("Evaluator Class Started");
	}
	public static double eval(String expression){
		System.out.println(expression);
		Double result = evaluator.evaluate(expression);
	    // Ouput the result
//	    System.out.println(expression + " = " + result.doubleValue());
	    
	    return result.doubleValue();
		
	}

}
