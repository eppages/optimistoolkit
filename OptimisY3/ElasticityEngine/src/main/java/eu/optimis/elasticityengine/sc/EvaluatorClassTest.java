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
import static org.junit.Assert.*;
import eu.optimis.elasticityengine.sc.EvaluatorClass;
import org.junit.Test;

public class EvaluatorClassTest {

	@Test
	public void testEvaluatorClass() {
	    EvaluatorClass evaluator = new EvaluatorClass();
	    assertEquals(true, evaluator.hashCode()!=0);
	    
	}

	@Test
	public void testEval() {
	    EvaluatorClass evaluator = new EvaluatorClass();
	    assertEquals(34969.0 , evaluator.eval("187*187"),0.1);	}

}
