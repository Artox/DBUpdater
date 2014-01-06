/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL.Reader;

/**
 * This is a stack-like character buffer for appending and removing elements.
 * Can be converted to a String
 */
public class ResizingCharacterBuffer {
	private int capacity;
	private char[] data;
	private int top;

	public ResizingCharacterBuffer() {
		// initial capacity is 1; bad idea? rather 2? idc.
		capacity = 1;
		data = new char[capacity];
		top = 0; //
	}

	public void append(char c) {
		// array full?
		if (top == capacity) {
			// double the space
			resize(capacity * 2);
		}

		// insert
		data[top++] = c;
	}

	public int count() {
		return top;
	}

	public char remove() {
		// array empty?
		if (top == 0) {
			throw new java.util.NoSuchElementException();
		}

		char c = data[--top];

		// resize if makes sense?
		// TODO!

		// return value
		return c;
	}

	public void clear() {
		top = 0;
	}

	public String toString() {
		if (top == 0) {
			return "";
		} else {
			return new String(data, 0, top);
		}
	}

	private void resize(int newsize) {
		// create new storage
		char[] newdata = new char[newsize];

		// copy all existing elements
		for (int i = 0; i < top; i++) {
			newdata[i] = data[i];
		}

		// apply new storage
		capacity = newsize;
		data = newdata;
	}
}
