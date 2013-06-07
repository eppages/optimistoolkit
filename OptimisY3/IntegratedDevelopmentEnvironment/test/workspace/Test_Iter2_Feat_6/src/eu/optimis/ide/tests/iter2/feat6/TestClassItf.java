package eu.optimis.ide.tests.iter2.feat6;

import integratedtoolkit.types.annotations.Method;
import java.lang.String;
import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.Parameter.*;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Constraints;

public interface TestClassItf{

	@Constraints(storageElemSize = 5f)
	@Method(declaringClass = "TestCE", isModifier = true, isInit = false)
	public String test_ce_1(@Parameter(direction = Direction.IN)  String in);

	@Constraints(storageElemSize = 5f, protectedStorageSpace = 5f)
	@Method(declaringClass = "TestCE", isModifier = true, isInit = false)
	public void test_ce_2_encrypted();
}
