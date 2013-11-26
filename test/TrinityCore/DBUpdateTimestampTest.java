/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package TrinityCore;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import TrinityCore.Exceptions.DBUpdateTimestampInvalidException;

@RunWith(JUnit4.class)
public class DBUpdateTimestampTest {
	private ArrayList<String> samples;

	@Before
	public void create_samples() throws IOException, URISyntaxException {
		// read them from file
		Path p = Paths.get(getClass().getResource("dbupdates.txt").toURI());
		File file = p.toFile();
		FileReader in = new FileReader(file);
		BufferedReader reader = new BufferedReader(in);

		samples = new ArrayList<String>();

		String line = reader.readLine();
		while (line != null) {
			samples.add(line);
			line = reader.readLine();
		}

		reader.close();
	}

	@Test
	public void create() throws DBUpdateTimestampInvalidException {
		try {
			for (String name : samples) {
				DBUpdateTimestamp ts = new DBUpdateTimestamp(name);
				assertNotNull(ts);
			}
		} catch (DBUpdateTimestampInvalidException e) {
			fail();
		}
	}

	@Test
	public void sort() {
		// create a copy, shuffle it, sort it and then compare to original
		@SuppressWarnings("unchecked")
		ArrayList<String> copy = (ArrayList<String>) samples.clone();

		Collections.shuffle(copy);

		Collections.sort(copy);

		Iterator<String> itra, itrb;
		itra = samples.iterator();
		itrb = copy.iterator();
		while (itra.hasNext() && itrb.hasNext()) {
			String a = itra.next();
			String b = itrb.next();
			assertSame(a, b);
		}
		assertFalse(itra.hasNext());
		assertFalse(itrb.hasNext());
	}
}
