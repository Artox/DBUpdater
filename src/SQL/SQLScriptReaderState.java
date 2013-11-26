/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

enum SQLScriptReaderState {
	DEFAULT,
	SINGLELINE_COMMENT,
	MULTILINE_COMMENT,
}
