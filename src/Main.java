/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

import UI.InteractiveTerminal;

public class Main {
	public static void main(String[] args) {
		InteractiveTerminal terminal = new InteractiveTerminal(System.console());
		terminal.run();
	}
}
