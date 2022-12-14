package org.openblock.creator.reader.single.field;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openblock.creator.code.call.Returnable;
import org.openblock.creator.code.call.returntype.ReturnType;
import org.openblock.creator.code.clazz.type.BasicType;
import org.openblock.creator.code.line.primitive.StringConstructor;
import org.openblock.creator.code.variable.IVariable;
import org.openblock.creator.code.variable.VariableCaller;
import org.openblock.creator.code.variable.field.Field;
import org.openblock.creator.code.variable.field.InitiatedField;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.impl.java.clazz.JavaClass;
import org.openblock.creator.project.Project;

import java.util.List;

public class FieldsClassReaderTests {

	private static final String[] classAsString = {"package org.openblock.creator.reader;",
			"",
			"public class EmptyClassReader {",
			"\tString helloWorld = \"Hello World\";",
			"\tString final grabber = helloWorld;",
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
	public void testReadFields() {
		Assertions.assertEquals(2, customClass.getFields().size());
	}

	@Test
	public void testReadFieldName() {
		if (customClass.getFields().isEmpty()) {
			Assertions.fail("Fields cannot be empty");
			return;
		}
		@NotNull List<Field> fields = customClass.getFields();

		Assertions.assertEquals("helloWorld", fields.get(0).getName());
		Assertions.assertEquals("grabber", fields.get(1).getName());
	}

	@Test
	public void testReadFieldFinals() {
		if (customClass.getFields().isEmpty()) {
			Assertions.fail("Fields cannot be empty");
			return;
		}
		@NotNull List<Field> fields = customClass.getFields();

		Assertions.assertFalse(fields.get(0).isFinal());
		Assertions.assertTrue(fields.get(1).isFinal());
	}

	@Test
	public void testReadFieldType() {
		if (customClass.getFields().isEmpty()) {
			Assertions.fail("Fields cannot be empty");
			return;
		}

		List<Field> fields = customClass.getFields();

		for (Field field : fields) {
			ReturnType returning = field.getReturnType();

			Assertions.assertFalse(returning.isArray());
			Assertions.assertTrue(returning.getType() instanceof BasicType);

			BasicType type = (BasicType) returning.getType();

			Assertions.assertEquals(new JavaClass(String.class), type.getTargetClass());
		}
	}

	@Test
	public void testReadFieldOneValue() {
		if (customClass.getFields().isEmpty()) {
			Assertions.fail("Fields cannot be empty");
			return;
		}

		@NotNull List<Field> fields = customClass.getFields();

		Field field = fields.get(0);
		if (!(field instanceof InitiatedField iField)) {
			Assertions.fail("field must be initiated");
			return;
		}
		Returnable.ReturnableLine code = iField.getAssigned();
		if (!(code instanceof StringConstructor stringLine)) {
			Assertions.fail("field should be a StringConstructor, yet found " + code.getClass().getName());
			return;
		}
		Assertions.assertEquals("Hello World", stringLine.getValue());
	}

	@Test
	public void testReadFieldTwoValue() {
		if (customClass.getFields().isEmpty()) {
			Assertions.fail("Fields cannot be empty");
			return;
		}

		@NotNull List<Field> fields = customClass.getFields();

		Field field = fields.get(1);
		if (!(field instanceof InitiatedField iField)) {
			Assertions.fail("field must be initiated");
			return;
		}
		Returnable.ReturnableLine code = iField.getAssigned();
		if (!(code instanceof VariableCaller<? extends IVariable> variableCaller)) {
			Assertions.fail("field should be a VariableCaller, yet found " + code.getClass().getName());
			return;
		}
		IVariable callable = variableCaller.getCallable();
		if (!(callable instanceof InitiatedField otherField)) {
			Assertions.fail(
					"Field should be a FieldCaller, yet found " + callable.getClass().getName()
			);
			return;
		}

		Assertions.assertEquals(fields.get(0), otherField);

	}

}
