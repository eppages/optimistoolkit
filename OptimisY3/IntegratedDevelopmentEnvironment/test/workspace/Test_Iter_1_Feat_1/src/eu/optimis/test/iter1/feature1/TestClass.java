package eu.optimis.test.iter1.feature1;

import javax.jws.WebService;
import javax.jws.WebMethod;

import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Orchestration;
import java.lang.String;
import javax.jws.WebParam;

@WebService
public class TestClass{

	@WebMethod
	@Orchestration
	public void testOE(@WebParam(name="input") String input){
	
	}

}
