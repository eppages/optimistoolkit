package eu.optimis.ide.tests.iter3.feat8;

import javax.jws.WebService;
import javax.jws.WebMethod;
import integratedtoolkit.types.annotations.Orchestration;
import javax.jws.WebParam;
import integratedtoolkit.types.annotations.Constraints;

@WebService
public class TestOrchClass{

	@Constraints(processorCPUCount = 1)
	@WebMethod
	@Orchestration
	public void testOE(){
	
	}
}
