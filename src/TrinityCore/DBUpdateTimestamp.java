/*
 * Copyright (c) 2013 - 2014 Artox

 * See the file LICENSE.txt for copying permission.
 */

package TrinityCore;

import java.util.InputMismatchException;
import java.util.Scanner;

import TrinityCore.Exceptions.DBUpdateTimestampInvalidException;

public class DBUpdateTimestamp implements Comparable<DBUpdateTimestamp> {
	public DBUpdateTimestamp(int year, int month, int day, int count)
			throws DBUpdateTimestampInvalidException {
		this.year = year;
		this.month = month;
		this.day = day;
		this.count = count;

		if (year < 0 || month < 0 || day < 0 || count < 0) {
			throw new DBUpdateTimestampInvalidException();
		}
	}

	// parses the filename to extract the timestamp
	// timestamp format: %uuuu_%uu_%uu_%db_xxxxxx
	public DBUpdateTimestamp(String filename)
			throws DBUpdateTimestampInvalidException {

		try {
			Scanner sc = new Scanner(filename);
			sc.useDelimiter("_");

			year = sc.nextInt();
			month = sc.nextInt();
			day = sc.nextInt();
			count = sc.nextInt();

			sc.close();
		} catch (InputMismatchException e) {
			throw new DBUpdateTimestampInvalidException();
		}

		if (year < 0 || month < 0 || day < 0 || count < 0) {
			throw new DBUpdateTimestampInvalidException();
		}
	}

	@Override
	public int compareTo(DBUpdateTimestamp o) {
		int t = this.year - o.year;
		int u = this.month - o.month;
		int v = this.day - o.day;
		int w = this.count - o.count;
		if (t != 0)
			return t;
		if (u != 0)
			return u;
		if (v != 0)
			return v;
		if (w != 0)
			return w;
		return 0;
	}

	@Override
	public String toString() {
		return Integer.toString(day) + '.' + Integer.toString(month) + '.' + Integer.toString(year) + '-' + Integer.toString(count);
	}

	private int year, month, day, count;
}
