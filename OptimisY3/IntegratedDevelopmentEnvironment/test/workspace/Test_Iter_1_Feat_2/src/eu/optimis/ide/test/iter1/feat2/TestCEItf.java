package eu.optimis.ide.test.iter1.feat2;

import integratedtoolkit.types.annotations.Method;
import java.lang.String;
import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.Parameter.*;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Constraints;

public interface TestCEItf{

	@Constraints(licenseTokens = "Genewise")
	@Method(declaringClass = "TestCE", isModifier = true, isInit = false)
	public void testCE_license(@Parameter(direction = Direction.IN)  String in);
}
