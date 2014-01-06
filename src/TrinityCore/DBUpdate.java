/*
 * Copyright (c) 2013 - 2014 Artox

 * See the file LICENSE.txt for copying permission.
 */

package TrinityCore;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.InputMismatchException;

import TrinityCore.Exceptions.DBUpdateNotAFileException;
import TrinityCore.Exceptions.DBUpdateInvalidFilenameException;
import TrinityCore.Exceptions.DBUpdateNotReadableException;
import TrinityCore.Exceptions.DBUpdateTimestampInvalidException;

public class DBUpdate implements Comparable<DBUpdate> {
	private Path path;
	private DBUpdateTimestamp timestamp;
	private String db;
	private String name;

	public DBUpdate(Path path) throws DBUpdateInvalidFilenameException,
			DBUpdateNotAFileException, DBUpdateNotReadableException {
		this.path = path;

		// check permissions
		File f = path.toFile();
		if (!f.isFile()) {
			throw new DBUpdateNotAFileException();
		}
		if (!f.canRead()) {
			throw new DBUpdateNotReadableException();
		}

		// parse the string
		Scanner sc = new Scanner(path.getFileName().toString());
		try {
			sc.useDelimiter("_");
			int y = sc.nextInt();
			int m = sc.nextInt();
			int d = sc.nextInt();
			int c = sc.nextInt();
			this.timestamp = new DBUpdateTimestamp(y, m, d, c);

			this.db = sc.next();

			sc.useDelimiter(".");
			this.name = sc.next();

			sc.close();
		} catch (InputMismatchException e) {
			sc.close();
			throw new DBUpdateInvalidFilenameException();
		} catch (DBUpdateTimestampInvalidException e) {
			sc.close();
			throw new DBUpdateInvalidFilenameException();
		}
	}

	public Path getPath() {
		return path;
	}

	public String getFileName() {
		return path.getFileName().toString();
	}

	@Override
	public int compareTo(DBUpdate o) {
		return this.timestamp.compareTo(o.timestamp);
		// TODO: sort by
		// db
		// name (alphabetically, length?)
	}
}
