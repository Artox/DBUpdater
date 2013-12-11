/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLJDBCConnection implements Database {
	private static boolean driverLoaded = false;
	private Console console;
	private Connection conn;

	public MySQLJDBCConnection(Console console) {
		this.console = console;
		conn = null;
	}

	private void loadDriver() {
		if (driverLoaded)
			return;

		try {
			// The newInstance() call is a work around for some
			// broken Java implementations

			Class.forName("com.mysql.jdbc.Driver").newInstance();

			driverLoaded = true;
		} catch (Exception ex) {
			// handle the error
			ex.printStackTrace();
		}
	}

	@Override
	public boolean open(String hostname, String port, String database,
			String username, String password) {
		// load driver if necessary
		loadDriver();

		// if the driver failed to load we need to cancel
		if (!driverLoaded)
			return false;

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

	@Override
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			console.printf("SQLException: %s\n", e.getMessage());
			console.printf("SQLState: %s\n", e.getSQLState());
			console.printf("SQLVendorError: %s\n", e.getErrorCode());
		}
		conn = null;
	}

	@Override
	public boolean executeScript(Path path) {
		// if the connection does not exist, fail
		if (conn == null)
			return false;

		File file = path.toFile();
		InputStream in;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			console.printf("File %s not Found!\n", file.getName());
			return false;
		}
		InputStreamReader reader = new InputStreamReader(in);

		ScriptRunner runner = new ScriptRunner(conn);
		runner.setLogWriter(console.writer());
		runner.setErrorLogWriter(console.writer());
		runner.setStopOnError(true);

		console.printf("Executing Update %s ...   ", path.getFileName()
				.toString());

		try {
			runner.runScript(reader);

			console.printf("done\n");
			return true;
		} catch (RuntimeSqlException e) {
			return false;
		}
	}
}
