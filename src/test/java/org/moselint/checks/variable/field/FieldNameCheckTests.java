package org.moselint.checks.variable.field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.moselint.MoseLint;
import org.moselint.check.Checkers;
import org.moselint.exception.CheckException;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.Nameable;
import org.openblock.creator.code.variable.field.Field;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.project.Project;

public class FieldNameCheckTests {

	private static final String[] classAsString = {"package org.openblock.creator.reader;",
			"",
			"public class EmptyClassReader {",
			"\tString helloWorld = \"Hello World\";",
			"\tString final grabber = helloWorld;",
			"\tString INCORRECT_FORMAT = \"IncorrectFormat\";",
			"}"};
	private static final Project<AbstractCustomClass> project = new Project<>("Test");
	private static final CustomClassReader reader = new CustomClassReader(classAsString);

	private static AbstractCustomClass customClass;

	@BeforeAll
	public static void init() {
		customClass = reader.readStageOne(project);
		customClass = reader.readStageTwo(project, customClass);
		customClass = reader.readStageThree(project, customClass);
	}

	@Test
	public void testFieldOneForCorrectFormat() {
		//setup
		Field field = customClass.getFields().get(0);

		//test and assert
		Assertions.assertDoesNotThrow(() -> {
			new MoseLint().checkFor(field, Checkers.FIELD_NAME);
		});
	}

	@Test
	public void testFieldThreeForInvalidFormat() {
		//setup
		Field field = customClass.getFields().get(2);
		CheckException e;

		//test
		try {
			new MoseLint().checkFor(field, Checkers.FIELD_NAME);
			Assertions.fail("Did not throw check exception");
			return;
		} catch (CheckException ex) {
			e = ex;
		}

		//assert
		Assertions.assertEquals(1, e.getContext().length);
		Assertions.assertEquals("underscore should not be used in variable names unless it is final static",
				e.getContext()[0].getMessage());
		Assertions.assertEquals(1, e.getContext()[0].getErrors().size());
		Assertions.assertEquals(customClass.getFields().get(2), e.getContext()[0].getErrors().get(0));
		Assertions.assertEquals(1, e.getContext()[0].getSuggestions().size());
		Codeable code = e.getContext()[0].getSuggestions().iterator().next();
		Assertions.assertInstanceOf(Field.class, code);
		Nameable fieldSuggestion = (Nameable) code;
		Assertions.assertEquals("incorrectFormat", fieldSuggestion.getName());

	}


}
