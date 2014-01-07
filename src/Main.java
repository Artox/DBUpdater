/*
 * Copyright (c) 2013 - 2014 Artox

 * See the file LICENSE.txt for copying permission.
 */

import UI.InteractiveTerminal;

public class Main {
	public static void main(String[] args) {
		InteractiveTerminal terminal = new InteractiveTerminal(System.console());
		terminal.run();

		/*
		// Code to test-run the SQL script reader
		try {
			String path = "sample.sql";
			SQL.Reader.ScriptReader reader = new SQL.Reader.ScriptReader(
					new java.io.InputStreamReader(new java.io.FileInputStream(
							new java.io.File(path))));
			reader.setSkipLeadingNewline(true);
			while (reader.parseNextQuery()) {
				System.out.print(reader.getQuery());
			}
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		*/
	}
}
