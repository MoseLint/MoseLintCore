package org.moselint.check.variable.field;

import org.moselint.exception.CheckException;
import org.moselint.exception.CheckExceptionContext;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.variable.field.Field;

public class FieldNameCheck implements FieldCheck {
	@Override
	public void isValid(Codeable codeable) throws CheckException {
		if (!canCheck(codeable)) {
			throw new RuntimeException("Failed to do checks before hand");
		}
		Field field = (Field) codeable;
		String name = field.getName();
		if (field.isFinal() && field.isStatic()) {
			if (name.chars().anyMatch(Character::isLowerCase)) {
				CheckExceptionContext context = new CheckExceptionContext(
						"static final fields should use all uppercase names separated by underscores");
				throw new CheckException(context);
			}
			return;
		}
		if (name.contains("_")) {
			CheckExceptionContext context = new CheckExceptionContext(
					"underscore should not be used in variable names unless it is final static");
			StringBuilder builder = new StringBuilder();
			boolean nextShouldBeUpper = false;
			for (int at = 0; at < name.length(); at++) {
				char charAt = name.charAt(at);
				if (Character.isLetter(charAt)) {
					if (nextShouldBeUpper) {
						builder.append(Character.toUpperCase(charAt));
						nextShouldBeUpper = false;
						continue;
					}
					builder.append(Character.toLowerCase(charAt));
					continue;
				}
				if (charAt == '_') {
					nextShouldBeUpper = true;
					continue;
				}
				builder.append(charAt);
			}

			Field suggestingField = field.toBuilder().setName(builder.toString()).build();
			context.getSuggestions().add(suggestingField);
			context.getErrors().add(field);

			throw new CheckException(context);
		}

	}

	@Override
	public String getDisplayName() {
		return "Field name";
	}
}
