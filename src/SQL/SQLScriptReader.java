package SQL;

import java.nio.file.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SQLScriptReader {
	// API
	public SQLScriptReader(Path path) throws FileNotFoundException {
		// take file as option
		// System.out.println("Looking at " + path.toAbsolutePath());
		File file = path.toFile();
		in = new FileInputStream(file);
		this.reader = new InputStreamReader(in);
		line = 0;
		querybuffer = new CharacterBuffer();
		commentbuffer1 = new CharacterBuffer();
		commentbuffer2 = new CharacterBuffer();
		state = SQLScriptReaderState.DEFAULT;
		_query = null;
	}

	// returns valid query only if a previous call to hasNextQuery() returned
	// true
	public Query nextQuery() {
		// return and delete the query
		Query r = _query;
		_query = null;
		return r;
	}

	public boolean hasNextQuery() {
		// if a query has already been found earlier, return true
		if (_query != null)
			return true;

		// Handle EOF
		if (reader == null)
			return false;

		// try to parse next query
		try {
			parseNextQuery();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// I a query was found, its now inside _query
		return _query != null;
	}

	// input reader
	FileInputStream in;
	InputStreamReader reader;
	int line;
	CharacterBuffer querybuffer;
	CharacterBuffer commentbuffer1;
	CharacterBuffer commentbuffer2;

	// internal state
	SQLScriptReaderState state;

	// output data
	Query _query;

	// logic
	private void parseNextQuery() throws IOException {
		int startline = line;

		int x = reader.read();
		while (x != -1) {
			char c = (char) x;

			// count EOLs
			if (c == '\n') {
				line++;
			}

			if (state == SQLScriptReaderState.DEFAULT) {
				// wait for ;, -- or /*
				if (c == ';') {
					// query ended

					// save query and comments
					_query = new Query(startline, querybuffer.toString(),
							commentbuffer1.toString(),
							commentbuffer2.toString());

					// clear buffers for next query
					querybuffer.clear();
					commentbuffer1.clear();
					commentbuffer2.clear();

					// query found, return
					return;

					// if currentchar = '-' and lastchar = '-' --> "--"
				} else if (c == '-' && buffertopequals(querybuffer, '-')) {
					// singleline-comment starts
					// remove the first '-' from querybuffer
					try {
						querybuffer.pop();
					} catch (CharacterBufferEmptyException e) {
						e.printStackTrace();
						System.exit(1);
					}

					// enter new state
					state = SQLScriptReaderState.SINGLELINE_COMMENT;

					// if currentchar = '*' and lastchar = '/' --> "/*"
				} else if (c == '*' && buffertopequals(querybuffer, '/')) {
					// multiline-comment starts
					// remove the '*' from querybuffer
					try {
						querybuffer.pop();
					} catch (CharacterBufferEmptyException e) {
						e.printStackTrace();
						System.exit(1);
					}

					// enter new state
					state = SQLScriptReaderState.MULTILINE_COMMENT;

					// just another char to add to the query
				} else {
					// Skip some characters/sequences for readability

					// EOLs
					if (c == '\n') {
						c = ' ';
					}

					// Windows line feed
					if (c == '\r') {
						c = ' ';
					}

					// multiple spaces
					if (c == ' ' && buffertopequals(querybuffer, ' ')) {
						// skip
					} else {
						// keep current character
						querybuffer.push(c);
					}
				}

			} else if (state == SQLScriptReaderState.SINGLELINE_COMMENT) {
				// wait for EOL
				if (c == '\n') {
					// comment ends
					state = SQLScriptReaderState.DEFAULT;

					// just another char to add to the comment
				} else {
					commentbuffer1.push(c);
				}

			} else if (state == SQLScriptReaderState.MULTILINE_COMMENT) {
				// wait for */
				if (c == '/' && buffertopequals(commentbuffer2, '*')) {
					// comment ends
					// remove * from commentbuffer
					try {
						commentbuffer2.pop();
					} catch (CharacterBufferEmptyException e) {
						e.printStackTrace();
						System.exit(1);
					}

					// enter new state
					state = SQLScriptReaderState.DEFAULT;

					// just another char to add to the comment
				} else {
					commentbuffer2.push(c);
				}

			}

			// proceede with next character
			x = reader.read();
		}
		// If this while-loop terminates, we have reached EOF
		// free up all buffers. They are not needed anymore

		querybuffer = null;
		commentbuffer1 = null;
		commentbuffer2 = null;

		// The InputReader can be closed too
		reader.close();
		in.close();

		// set it to null also. THis indicates that EOF was reached
		reader = null;
		in = null;
	}

	private boolean buffertopequals(CharacterBuffer buffer, char c) {
		char d;
		try {
			d = buffer.peek();
		} catch (CharacterBufferEmptyException e) {
			return false;
		}
		return c == d;
	}
}
