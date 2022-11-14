package org.moselint.check;

import org.moselint.check.variable.field.FieldNameCheck;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Checkers {

	FieldNameCheck FIELD_NAME = new FieldNameCheck();

	static Collection<Checker> getCheckers() {
		return Arrays.stream(Checkers.class.getDeclaredFields())
				.parallel()
				.filter(field -> Modifier.isFinal(field.getModifiers()))
				.filter(field -> Modifier.isStatic(field.getModifiers()))
				.filter(field -> Modifier.isPublic(field.getModifiers()))
				.filter(field -> field.getType().isAssignableFrom(Checker.class))
				.map(field -> {
					try {
						return (Checker) field.get(null);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						//noinspection ReturnOfNull
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
}
