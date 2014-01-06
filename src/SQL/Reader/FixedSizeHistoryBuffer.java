/*
 * Copyright (c) 2013 - 2014 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL.Reader;

import java.util.NoSuchElementException;

/**
 * This is a very interesting class. It is supposed to provide access the last n
 * elements appended to it
 */
public class FixedSizeHistoryBuffer<T> {
	private Object[] data;
	private NonOverflowingInteger count;
	private OverflowingInteger top;

	/**
	 * Creates a new history with the given size
	 * 
	 * @param size
	 *            number of elements to keep available
	 */
	public FixedSizeHistoryBuffer(int size) {
		data = new Object[size];
		count = new NonOverflowingInteger(0, size, 0);
		top = new OverflowingInteger(0, size-1, size-1);
		//System.out.println("Initializing top with " + top.getValue());
	}

	/**
	 * appends a new value to the buffer
	 * 
	 * @param value
	 *            value
	 */
	public void append(T value) {
		// I simply append a new element above current top
		count.increment(1);
		top.increment(1);
		data[top.getValue()] = value;
		//System.out.println("Inserting data at " + top.getValue());
	}

	/**
	 * gets the i-last value (from 0 to size)
	 * 
	 * @param i
	 *            depth of history (0 == most current)
	 * @return i-latest value
	 */
	@SuppressWarnings("unchecked")
	public T getLast(int i) {
		// check if we have enough history
		if (i >= count.getValue()) {
			throw new NoSuchElementException();
		}

		// fetch the wanted value
		OverflowingInteger position = top.clone();
		position.decrement(i);
		//System.out.println("Reading data at " + position.getValue());
		return (T) data[position.getValue()];
	}
}
