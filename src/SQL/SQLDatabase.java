package SQL;

import java.io.Console;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class SQLDatabase {
	private Console console;
	private Connection conn;

	public SQLDatabase(Console console) {
		this.console = console;
		conn = null;
	}

	public static boolean init() {
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations

			Class.forName("com.mysql.jdbc.Driver").newInstance();

			return true;
		} catch (Exception ex) {
			// handle the error
			ex.printStackTrace();

			return false;
		}
	}

	public boolean connectToMySQL(String hostname, String port,
			String database, String username, String password) {
		try {
			String jdbcpath = "jdbc:mysql://" + hostname + ":" + port + "/"
					+ database;

			conn = DriverManager.getConnection(jdbcpath, username, password);

			return true;
		} catch (SQLException e) {
			// handle any errors
			console.printf("SQLException: %s\n", e.getMessage());
			console.printf("SQLState: %s\n", e.getSQLState());
			console.printf("SQLVendorError: %s\n", e.getErrorCode());

			return false;
		}
	}

	public void disconnect() {
		try {
			conn.close();
		} catch (SQLException e) {
			console.printf("SQLException: %s\n", e.getMessage());
			console.printf("SQLState: %s\n", e.getSQLState());
			console.printf("SQLVendorError: %s\n", e.getErrorCode());
		}
		conn = null;
	}

	public boolean executeSQLScript(Path path) {
		SQLScriptReader reader;
		try {
			reader = new SQLScriptReader(path);
		} catch (FileNotFoundException e) {
			return false;
		}

		console.printf("Executing Update %s ...   ", path.getFileName()
				.toString());

		while (reader.hasNextQuery()) {
			Query query = reader.nextQuery();
			try {
				Statement stmt = (Statement) conn.createStatement();
				stmt.execute(query.getQuery());
				stmt.close();
			} catch (SQLException e) {
				console.printf("Encountered error in query on line %d:\n",
						query.getLineNumber());
				console.printf("SQLException: %s\n", e.getMessage());
				console.printf("SQLState: %s\n", e.getSQLState());
				console.printf("SQLVendorError: %s\n", e.getErrorCode());
				return false;
			}

		}

		console.printf("done\n");

		return true;
	}
}
