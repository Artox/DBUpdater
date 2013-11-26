/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;

public class MySQLClientWrapper {
	private Console console;
	private String hostname;
	private String port;
	private String database;
	private String username;
	private String password;

	public static boolean init() {
		return true;
	}

	public MySQLClientWrapper(Console console) {
		this.console = console;
		this.hostname = null;
		this.port = null;
		this.database = null;
		this.username = null;
		this.password = null;
	}

	public boolean connect(String hostname, String port, String database,
			String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		return true;
	}

	public void disconnect() {
		this.hostname = null;
		this.port = null;
		this.database = null;
		this.username = null;
		this.password = null;
	}

	public boolean executeSQLScript(Path path) {
		console.printf("Executing Update %s ...   ", path.getFileName()
				.toString());

		// create process
		ProcessBuilder pb = new ProcessBuilder("mysql", "--host=" + hostname,
				"--port=" + port, "--database=" + database, "--user="
						+ username, "--password=" + password);
		pb.redirectErrorStream();
		// TODO: pass password to process stdin instead

		Process p;
		try {
			p = pb.start();
		} catch (IOException e1) {
			console.printf("Failed to start the mysql client!\n");
			return false;
		}

		// get stdin of mysql
		OutputStream pstdin = p.getOutputStream();

		// MySQL process is ready

		// open sql script
		InputStream fin;
		try {
			fin = new FileInputStream(path.toFile());
		} catch (FileNotFoundException e1) {
			console.printf("Failed to open the sql script!\n");

			// end mysql
			try {
				pstdin.close();
			} catch (IOException e) {
			}

			// fail
			return false;
		}

		// copy the file to stdin of mysql
		try {
			int b = fin.read();
			while (b != -1) {
				pstdin.write(b);
				b = fin.read();
			}
			// TODO: read and write more bytes at once for more speed
		} catch (IOException e1) {
			console.printf("Failed to read/write the sql script!\n");

			// end mysql
			try {
				pstdin.close();
			} catch (IOException e) {
			}

			// fail
			return false;
		}

		// close sql script
		try {
			fin.close();
		} catch (IOException e1) {
			console.printf("Failed to close the sql script!\n");
		}

		// end mysql
		try {
			pstdin.close();
		} catch (IOException e1) {
			console.printf("Failed to close the mysql stdin pipe!\n");
		}

		// wait for process to finish
		int s;
		try {
			s = p.waitFor();
		} catch (InterruptedException e1) {
			console.printf("Somehow we were interrupted while waiting for mysql. This should NEVER happen!\n");
			console.printf("So I am killing mysql now!\n");
			p.destroy();
			return false;
		}

		// check result
		if (s == 0) {
			// all fine (I hope)
			console.printf("done\n");

			// success
			return true;
		} else {
			// so the exit-status is non-zero
			console.printf("failed\n");

			// fetch and print the output
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			try {
				String line = reader.readLine();
				while (line != null) {
					console.printf("%s\n", line);
					line = reader.readLine();
				}
			} catch (IOException e) {
				console.printf("Failed reading mysql process output!\n");
			}

			try {
				reader.close();
			} catch (IOException e1) {
				console.printf("Failed to close process output stream!\n");
			}

			// fail
			return false;
		}
	}
}
