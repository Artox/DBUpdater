/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL.Reader;

/**
 * This class provides an integer with custom minimum and maximum values
 * Operations will overflow there and restart from minimum/maximum
 */
public class OverflowingInteger {
	// constraints
	private int min;
	private int max;

	// value
	private int value;

	// range beetween min and max. Because I need it often.
	private int range;

	/**
	 * Initializes a new OverflowingInteger
	 * 
	 * @param min
	 *            minimum allowed value
	 * @param max
	 *            maximum allowed value
	 * @param value
	 *            actual value
	 */
	public OverflowingInteger(int min, int max, int value) {
		this.min = min;
		this.max = max;

		this.value = value;

		this.range = max - min;
	}

	/**
	 * returns the current value
	 * 
	 * @return current value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Increments the Number
	 * 
	 * @param by
	 *            amount to increment by
	 */
	public void increment(int by) {
		// if given value is negative, we have to decrement
		if (by < 0) {
			decrement(Math.abs(by));
			return;
		}

		// cut off full overflows
		by %= (range + 1);

		// check if we would still overflow
		int space_up = max - value;
		if (by > space_up) {
			// we check how big overflow is and add that to min
			by -= space_up + 1;
			value = min + by;
		} else {
			// no overflowing, this is easy
			value += by;
		}
	}

	/**
	 * Decrements the Number
	 * 
	 * @param by
	 *            amount to decrement by
	 */
	public void decrement(int by) {
		// if given value is negative, we have to increment
		if (by < 0) {
			decrement(Math.abs(by));
			return;
		}

		// cut off full overflows
		by %= (range + 1);

		// check if we would still overflow
		int space_down = value - min;
		if (by > space_down) {
			// we check how big overflow is and remove that from max
			by -= space_down + 1;
			value = max - by;
		} else {
			// no overflowing, this is easy
			value -= by;
		}
	}

	/**
	 * Creates a clone of this Object
	 * 
	 * @return Independent copy of this Object
	 */
	public OverflowingInteger clone() {
		return new OverflowingInteger(min, max, value);
	}
}
