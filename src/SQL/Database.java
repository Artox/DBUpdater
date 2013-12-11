/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

import java.nio.file.Path;

public interface Database {
	/**
	 * This function is supposed to perform any required one-time initialization.
	 * For Example, loading the MySQL JDBC Connector or checking if the client binary is available
	 * For the Future: I think this method can be replaced by code in teh cosntrucotrs of classes that need it
	 * @return wether initialization worked
	 */
	public boolean init();

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
