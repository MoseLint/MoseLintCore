package org.openblock.creator.reader.single.generic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.project.Project;

public class ClassGenericReaderTests {

	private static final String[] classAsString = {"package org.openblock.creator.reader;",
			"",
			"public final class EmptyClassReader<I> {",
			"}"};

	private static final Project<AbstractCustomClass> project = new Project<>("Test");
	private static final CustomClassReader reader = new CustomClassReader(classAsString);

	private static AbstractCustomClass customClass;

	@BeforeAll
	public static void init() {
		customClass = reader.readStageOne(project);
		customClass = reader.readStageTwo(project, customClass);
	}

	@Test
	public void testForSingleGeneric() {
		Assertions.assertEquals(1, customClass.getGenerics().size());
		Assertions.assertEquals("I", customClass.getGenerics().get(0).getName());
	}
}
