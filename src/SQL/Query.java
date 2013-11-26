/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

// reperesents a query found in sql script
public class Query {
	private int startline;
	private String query;
	private String comment1, comment2;

	Query(int startline, String query, String comment1, String comment2) {
		this.startline = startline;
		this.query = query;
		this.comment1 = comment1;
		this.comment2 = comment2;
	}

	public int getLineNumber() {
		return startline;
	}

	public String getQuery() {
		return query;
	}

	public String getPreComments() {
		return comment1;
	}

	public String getPostComments() {
		return comment2;
	}
}
