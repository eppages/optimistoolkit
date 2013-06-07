package eu.optimis.test.iter1.feature1;

import integratedtoolkit.types.annotations.Method;
import java.lang.String;
import integratedtoolkit.types.annotations.Parameter;
import integratedtoolkit.types.annotations.Parameter.*;
import integratedtoolkit.types.annotations.Constraints;
import integratedtoolkit.types.annotations.Service;

public interface TestClassItf{

	@Constraints(processorArchitecture = "i64")
	@Method(declaringClass = "TestCEClass", isModifier = true, isInit = false)
	public String testce(@Parameter(direction = Direction.IN)  String input);

	@Constraints(processorArchitecture = "i386")
	@Method(declaringClass = "TestCEClass", isModifier = true, isInit = false)
	public void testCE_2(@Parameter(direction = Direction.IN)  String in);

	@Constraints(processorCPUCount = 3)
	@Method(declaringClass = "TestCEClass", isModifier = true, isInit = false)
	public void testCE_3();

	@Service(name = "GeneDetectionService", namespace = "http://genedetection.bsc.es/", port = "GeneDetectionPort")
	public String detectGenes(@Parameter(direction = Direction.IN)  String genomeName, @Parameter(direction = Direction.IN)  String sequenceName, @Parameter(direction = Direction.IN)  int arg2, @Parameter(direction = Direction.IN)  int arg3, @Parameter(direction = Direction.IN)  float arg4);
}
