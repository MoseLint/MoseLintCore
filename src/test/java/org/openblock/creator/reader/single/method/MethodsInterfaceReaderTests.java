package org.openblock.creator.reader.single.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.openblock.creator.code.function.IFunction;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.project.Project;

import java.util.Iterator;

public class MethodsInterfaceReaderTests {

	public static final String[] classAsString = {
			"package org.openblock.creator.reader;",
			"",
			"public interface EmptyClassReader {",
			"\tvoid testMethod();",
			"\tvoid testMethodWithParameter(String name);",
			"\tdefault void defaultMethod(){",
			"\t\ttestMethod();",
			"\t}",
			"}"
	};

	private static final Project<AbstractCustomClass> project = new Project<>("Test");
	private static final CustomClassReader reader = new CustomClassReader(classAsString);

	private static AbstractCustomClass customClass;

	@BeforeAll
	public static void init() {
		customClass = reader.readStageOne(project);
		customClass = reader.readStageTwo(project, customClass);
		customClass = reader.readStageThree(project, customClass);
	}

	public void testMethodsCount(){
		Assertions.assertEquals(3, customClass.getFunctions().size());
		Iterator<IFunction> iterator = customClass.getFunctions().iterator();
		IFunction testMethod = iterator.next();
		IFunction testMethodWithParameter = iterator.next();
		IFunction defaultMethod = iterator.next();
		Assertions.assertEquals("testMethod", testMethod.getName());
		Assertions.assertEquals("testMethodWithParameter", testMethodWithParameter.getName());
		Assertions.assertEquals(1, testMethodWithParameter.getParameters().size());
		Assertions.assertEquals("name", testMethodWithParameter.getParameters().get(0).getName());
		Assertions.assertEquals("String", testMethodWithParameter.getParameters().get(0).getReturnType().getDisplayText());
		Assertions.assertEquals("defaultMethod", defaultMethod.getName());
	}
}
