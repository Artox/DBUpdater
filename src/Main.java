import UI.InteractiveTerminal;

public class Main {
	public static void main(String[] args) {
		InteractiveTerminal terminal = new InteractiveTerminal(System.console());
		terminal.run();
	}
}
