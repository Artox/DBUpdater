/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

import java.nio.file.Path;

public interface Database {
	/**
	 * This function is supposed to prepare the execution of queries on the given database
	 * @return wether the database could be opened
	 */
	public boolean open(String hostname, String port, String database, String username, String password);

	/**
	 * This function is supposed to execute the given SQL Script
	 * @return wether the execution was successful
	 */
	public boolean executeScript(Path path);

	/**
	 * This function is supposed to close the database
	 */
	public void close();
}
