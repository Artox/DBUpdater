/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL.Reader;

/**
 * This class proves an Integer with custom minimum and maximum values that
 * doesn't overflow. If minimum (maximum) is reached, decrement (increment) will
 * have no effect
 */
public class NonOverflowingInteger {
	// constraints
	private int min;
	private int max;

	// value
	private int value;

	/**
	 * Initializes a new NonOverflowingInteger
	 * 
	 * @param min
	 *            minimum allowed value
	 * @param max
	 *            maximum allowed value
	 * @param value
	 *            actual value
	 */
	public NonOverflowingInteger(int min, int max, int value) {
		this.min = min;
		this.max = max;

		this.value = value;
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

		// check if we would overflow
		int space_up = max - value;
		if (by > space_up) {
			// we simply use max
			value = max;
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

		// check if we would overflow
		int space_down = value - min;
		if (by > space_down) {
			// we simply use min
			value = min;
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
	public NonOverflowingInteger clone() {
		return new NonOverflowingInteger(min, max, value);
	}
}
