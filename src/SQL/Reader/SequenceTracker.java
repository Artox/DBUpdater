/*
 * Copyright (c) 2013 - 2014 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL.Reader;

import java.util.NoSuchElementException;

/**
 * This class tracks a sequence of characters against a stream of characters. It
 * knows at all times whether the tracked sequence is matched. The stream can be
 * fed using the update() method
 */
public class SequenceTracker {
	// sequence to look for
	private char[] _sequence;

	// keep history for lookups
	private FixedSizeHistoryBuffer<Character> _history;

	/**
	 * creates a new SequenceTracker for the given sequence
	 * 
	 * @param sequence
	 *            sequence to track
	 */
	public SequenceTracker(String sequence) {
		_sequence = sequence.toCharArray();

		_history = new FixedSizeHistoryBuffer<Character>(_sequence.length);
		// matchcount = 0;
	}

	/**
	 * Feeds the next character
	 * 
	 * @param c
	 *            next character
	 */
	public void update(char c) {
		_history.append(c);
	}

	/**
	 * Checks whether the sequence matches the current head of the flow
	 * 
	 * @return
	 */
	public boolean matches() {
		// How does this work?
		// First, always check if the top matches, that is end of sequence and
		// latest character
		// then go back into history

		// probably bad performance.
		// TODO: I have a better idea with 2 arrays, 1 for the sequence and 1
		// whether the specific character matches. Then, keep track on the fly
		// within update()

		int i = _sequence.length - 1;
		int j = 0;
		try {
			while (i >= 0) {
				char c = _history.getLast(j);

				// if it doesn't match, cancel
				if (c != _sequence[i]) {
					return false;
				}

				i--;
				j++;
			}
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}

	}
}
