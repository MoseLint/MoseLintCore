package org.moselint;

import org.moselint.check.Checker;
import org.moselint.exception.CheckException;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.statement.Statement;

import java.util.HashMap;
import java.util.Map;

public class MoseLint {

	public static void checkFor(Codeable codeable, Checker checker) throws CheckException {
		if (!checker.canCheck(codeable)) {
			return;
		}
		checker.isValid(codeable);
		if (codeable instanceof Statement statement) {
			for (Codeable line : statement.getCodeBlock()) {
				checkFor(line, checker);
			}
		}
	}

	public static Map<Checker, CheckException> checkForAll(Codeable codeable, Iterable<Checker> checkers) {
		Map<Checker, CheckException> checkExceptions = new HashMap<>();
		for (Checker checker : checkers) {
			try {
				checkFor(codeable, checker);
			} catch (CheckException e) {
				checkExceptions.put(checker, e);
			}
		}
		return checkExceptions;
	}

}
