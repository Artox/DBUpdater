package SQL;

public class CharacterBuffer {
	public CharacterBuffer() {
		top = null;
		bottom = null;
		size = 0;
	}

	public void push(Character c) {
		// add at end

		if (size == 0) {
			Element e = new Element();
			e.c = c;
			e.next = null;
			e.prev = null;
			top = e;
			bottom = e;
		} else {
			Element e = new Element();
			e.c = c;
			e.next = null;
			e.prev = bottom;
			bottom.next = e;
			bottom = e;
		}

		size++;
	}

	public Character pop() throws CharacterBufferEmptyException {
		if (size == 0)
			throw new CharacterBufferEmptyException();

		size--;

		if (size == 0) {
			Element e = bottom;
			top = null;
			bottom = null;
			return e.c;
		} else {
			Element e = bottom;
			bottom = e.prev;
			bottom.next = null;
			return e.c;
		}
	}

	public Character peek() throws CharacterBufferEmptyException {
		if (size == 0)
			throw new CharacterBufferEmptyException();

		return bottom.c;
	}

	@Override
	public String toString() {
		// Very Very bad code, will become horribly slow
		String s = new String();
		for (Element e = top; e != null; e = e.next) {
			s = s + e.c;
		}
		return s;
	}

	public void clear() {
		top = null;
		bottom = null;
		size = 0;
	}

	private class Element {
		Character c;
		Element next;
		Element prev;
	}

	private Element top, bottom;
	private int size;
}
