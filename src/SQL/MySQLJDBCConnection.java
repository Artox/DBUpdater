/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

import java.io.Console;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class MySQLJDBCConnection implements Database {
	private Console console;
	private Connection conn;

	public MySQLJDBCConnection(Console console) {
		this.console = console;
		conn = null;
	}

	@Override
	public boolean init() {
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

	@Override
	public boolean open(String hostname, String port,
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
		return false;

		/* console.printf("Executing Update %s ...   ", path.getFileName().toString());
		console.printf("done\n");
		return true; */
	}
}
