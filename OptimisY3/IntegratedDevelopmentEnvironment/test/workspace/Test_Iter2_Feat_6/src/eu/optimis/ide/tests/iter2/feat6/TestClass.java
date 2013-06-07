package eu.optimis.ide.tests.iter2.feat6;

import javax.jws.WebService;
import javax.jws.WebMethod;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Orchestration;
import java.lang.String;
import javax.jws.WebParam;
import integratedtoolkit.types.annotations.Constraints;

@WebService
public class TestClass{

	@Constraints(sharedStorageSpace = 5.0f)
	@WebMethod
	@Orchestration
	public void OE_test(@WebParam(name="in") String in){
	
	}
}
