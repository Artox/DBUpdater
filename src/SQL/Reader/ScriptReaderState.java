/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL.Reader;

public enum ScriptReaderState {
	DEFAULT,
	SSTRING,
	DSTRING,
	SLCOMMENT,
	MLCOMMENT,
}
