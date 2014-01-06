/*
 * Copyright (c) 2013 - 2014 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL.Reader;

import java.io.IOException;
import java.io.Reader;

public class ScriptReader {

	private Reader _reader;
	private String _query;

	public ScriptReader(Reader reader) {
		_reader = reader;
	}

	/**
	 * searches the next query in the file if any if it fails, getQuery() will
	 * return the last one found
	 * 
	 * @return true if a query was found, false when eof is reached
	 * @throws IOException
	 */
	public boolean parseNextQuery() throws IOException {

		// create a buffer to store the query
		ResizingCharacterBuffer buffer = new ResizingCharacterBuffer();

		// initialize state
		ScriptReaderState state = ScriptReaderState.DEFAULT;

		// create sequence trackers
		SequenceTracker tracker_querySeparator = new SequenceTracker(";");
		SequenceTracker tracker_slcommentStarter = new SequenceTracker("--");
		SequenceTracker tracker_slcommentTerminator = new SequenceTracker("\n");
		SequenceTracker tracker_mlcommentStarter = new SequenceTracker("/*");
		SequenceTracker tracker_mlcommentTerminator = new SequenceTracker("*/");
		SequenceTracker tracker_sstringSeparator = new SequenceTracker("'");
		SequenceTracker tracker_dstringSeparator = new SequenceTracker("\"");
		SequenceTracker tracker_escapeSymbol = new SequenceTracker("\\");

		// track escaped characters
		boolean escaped = false;

		int c = _reader.read();
		while (c != -1) {
			// update trackers
			tracker_querySeparator.update((char) c);
			tracker_sstringSeparator.update((char) c);
			tracker_dstringSeparator.update((char) c);
			tracker_slcommentStarter.update((char) c);
			tracker_slcommentTerminator.update((char) c);
			tracker_mlcommentStarter.update((char) c);
			tracker_mlcommentTerminator.update((char) c);
			tracker_escapeSymbol.update((char) c);

			// Now it depends on the current state which sequences matter
			// we need to check for sequences that will change the current state
			// or terminate the query
			switch (state) {
			case DEFAULT:
				// Looking for query end, strings or comments

				// 1) check if the query ended
				if (tracker_querySeparator.matches()) {
					// apply the current character
					buffer.append((char) c);

					// save the query
					_query = buffer.toString();

					// return success
					return true;
				}
				// 2) check if a comment starts
				// a) --
				else if (tracker_slcommentStarter.matches()) {
					state = ScriptReaderState.SLCOMMENT;

					// remove the comment-starter sequence from the buffer.
					// Its not part of a query
					buffer.remove();
					// removing only 1 character is sufficient since the second
					// one hadn't been pushed to the buffer
				}
				// b) /* */
				else if (tracker_mlcommentStarter.matches()) {
					state = ScriptReaderState.MLCOMMENT;

					// remove the comment-starter sequence from the buffer.
					// Its not part of a query
					buffer.remove();
					// removing only 1 character is sufficient since the second
					// one hadn't been pushed to the buffer
				}

				// 3) look for strings (if not escaped)
				// a) ''
				else if (tracker_sstringSeparator.matches()) {
					state = ScriptReaderState.SSTRING;

					// apply the current character
					buffer.append((char) c);
				}
				// b) ""
				else if (tracker_dstringSeparator.matches()) {
					state = ScriptReaderState.DSTRING;

					// apply the current character
					buffer.append((char) c);
				}
				// nothing special, just continue
				else {
					// apply the current character
					buffer.append((char) c);
				}
				break;
			case SLCOMMENT:
				// looking for end of single-line-comment (newline)
				if (tracker_slcommentTerminator.matches()) {
					// comments are skipped
					// but the newline is needed
					buffer.append((char) c);

					state = ScriptReaderState.DEFAULT;
				}
				break;
			case MLCOMMENT:
				// comments are skipped

				// looking for end of multi-line-comment
				if (tracker_mlcommentTerminator.matches()) {
					state = ScriptReaderState.DEFAULT;
				}
				break;
			case SSTRING:
				// apply the current character
				buffer.append((char) c);

				// looking for end of string
				if (!escaped && tracker_sstringSeparator.matches()) {
					state = ScriptReaderState.DEFAULT;
				}
				break;
			case DSTRING:
				// apply the current character
				buffer.append((char) c);

				// looking for end of string
				if (!escaped && tracker_dstringSeparator.matches()) {
					state = ScriptReaderState.DEFAULT;
				}
				break;
			default:
				throw new java.lang.RuntimeException(
						"Encountered unhandled ScriptReaderState");
			}

			// update escape status for next character
			escaped = !escaped && tracker_escapeSymbol.matches();

			// read next character
			c = _reader.read();
		}

		// well, we definitely failed to find a query terminator
		return false;
	}

	/**
	 * @return current query
	 */
	public String getQuery() {
		if (_query == null)
			throw new java.util.NoSuchElementException();

		return _query;
	}
}
