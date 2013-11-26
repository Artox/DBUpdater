package UI;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import SQL.MySQLClientWrapper;
//import SQL.SQLDatabase;
import TrinityCore.DBUpdate;
import TrinityCore.Exceptions.DBUpdateInvalidFilenameException;
import TrinityCore.Exceptions.DBUpdateNotAFileException;
import TrinityCore.Exceptions.DBUpdateNotReadableException;

public class InteractiveTerminal {
	private Console console;

	private String sqlhostname;
	private String sqlport;
	private String sqldatabase;
	private String sqlusername;
	private String sqlpassword;

	// private SQLDatabase db;
	private MySQLClientWrapper db;

	private Path updatesfolder;

	private ArrayList<DBUpdate> updates;

	private int first;
	private int last;

	public InteractiveTerminal(Console console) {
		this.console = console;
		sqlhostname = null;
		sqlport = null;
		sqldatabase = null;
		sqlusername = null;
		sqlpassword = null;
		updates = null;
		first = 0;
		last = 0;
	}

	public void run() {
		boolean check;

		// load mysql driver
		// check = SQLDatabase.init();
		check = MySQLClientWrapper.init();
		if (!check) {
			console.printf("Failed to laod MySQL Driver!\n");
			return;
		}

		check = false;
		while (!check) {
			// query database access
			requestDatabaseInfo();

			// SQLDatabase db = new SQLDatabase(console);
			MySQLClientWrapper db = new MySQLClientWrapper(console);
			check = db.connect(sqlhostname, sqlport, sqldatabase, sqlusername,
					sqlpassword);

			if (!check) {
				console.printf("Failed to connect to the database!\n");
			} else {
				this.db = db;
			}
		}

		check = false;
		while (!check) {

			// query updates path
			requestUpdatesPath();

			// Collect all updates
			collectUpdates();
			check = updates.size() != 0;
			if (!check) {
				console.printf("No updates found! Please use a different location.\n");
			}
		}

		// query for update range
		requestUpdateRange();

		// Print Summary
		printSelectedUpdates();

		// last chance
		check = requestConfirmation("This is your Last chance to abort!");
		if (!check) {
			console.printf("Canceled!\n");
			return;
		}

		check = applySelectedUpdates();
		if (!check) {
			console.printf("Finished with errors!\n");
		} else {
			console.printf("Finished!\n");
		}
	}

	private void requestDatabaseInfo() {
		sqlhostname = requestInputWithFallback("MySQL Hostname", "localhost");
		sqlport = requestInputWithFallback("MySQL Port", "3306");
		sqldatabase = requestInput("MySQL Database");
		sqlusername = requestInputWithFallback("MySQL User", "root");
		sqlpassword = requestPassword("MySQL Password");

		// TODO: test connection
	}

	private void requestUpdatesPath() {
		boolean valid = false;
		File f;
		while (!valid) {
			String path = requestInput("SQL Updates Folder");
			Path p = Paths.get(path);
			f = p.toFile();

			if (f.canRead() && f.isDirectory()) {
				// path found
				updatesfolder = p;
				return;
			} else {
				console.printf("Please enter a proper path and make sure it`s readable!\n");
			}
		}
	}

	private void requestUpdateRange() {

		int i = 0;
		for (DBUpdate u : updates) {
			console.printf("[%d]: %s\n", ++i, u.getFileName());
		}

		first = 0;
		while (first < 1) {
			String sfirst = requestInputWithFallback("First Update", "1");
			try {
				first = Integer.parseInt(sfirst);
			} catch (NumberFormatException e) {
				console.printf("Not a number!\n");
				first = 0;
			}

			if (first < 1 || first > i) {
				console.printf("Invalid index!\n");
				first = 0;
			}
		}

		last = 0;
		while (last < 1) {
			String slast = requestInputWithFallback("Last Update",
					Integer.toString(i));
			try {
				last = Integer.parseInt(slast);
			} catch (NumberFormatException e) {
				console.printf("Not a number!\n");
				last = 0;
			}

			if (last < first || last > i) {
				console.printf("Invalid index!\n");
				last = 0;
			}
		}
	}

	private void printSelectedUpdates() {
		console.printf("Going to apply the following updates: \n");

		for (int i = first - 1; i < last; i++) {
			DBUpdate u = updates.get(i);
			console.printf("%s\n", u.getFileName());
		}
	}

	private void collectUpdates() {
		console.printf("Scanning %s for updates ...\n",
				updatesfolder.toString());

		updates = new ArrayList<DBUpdate>();

		DirectoryStream<Path> files;
		try {
			files = Files.newDirectoryStream(updatesfolder);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		for (Path file : files) {
			// only use sql files
			if (file.getFileName().toString().endsWith(".sql")) {
				try {
					DBUpdate u = new DBUpdate(file);
					updates.add(u);
				} catch (DBUpdateInvalidFilenameException
						| DBUpdateNotAFileException
						| DBUpdateNotReadableException e) {
					console.printf("Skipping %s because of errors!\n",
							file.toString());
				}
			}
		}

		// sort
		Collections.sort(updates);
	}

	private boolean applySelectedUpdates() {
		for (int i = first - 1; i < last; i++) {
			DBUpdate u = updates.get(i);
			boolean check = db.executeSQLScript(u.getPath());
			if (!check) {
				return false;
			}
		}
		return true;
	}

	private String requestInput(String Prompt) {
		String r = "";
		while (r.isEmpty()) {
			r = console.readLine("%s: ", Prompt);

			if (r.isEmpty()) {
				console.printf("%s", "Please provide an answer!\n");
			}
		}

		return r;
	}

	private String requestPassword(String Prompt) {
		char[] p = console.readPassword("%s []: ", Prompt);
		String r = new String(p);

		// Security overwriting
		for (int i = 0; i < p.length; i++)
			p[i] = 0;

		return r;
	}

	private String requestInputWithFallback(String Prompt, String fallback) {
		console.printf("%s [%s]: ", Prompt, fallback);
		String r = console.readLine();

		if (r.isEmpty()) {
			r = fallback;
		}

		return r;
	}

	private boolean requestConfirmation(String action) {
		console.printf("%s\n", action);
		String s = console.readLine("Enter 'yes' to proceed: ");
		return s.equals("yes");
	}
}
