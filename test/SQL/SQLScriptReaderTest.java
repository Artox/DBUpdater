/*
 * Copyright (c) 2013 Artox

 * See the file LICENSE.txt for copying permission.
 */

package SQL;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SQLScriptReaderTest {
	@Test
	public void create() {
		SQLScriptReader reader;

		// null
		try {
			reader = new SQLScriptReader(null);
			fail();
		} catch (NullPointerException e) {
			assert (true);
		} catch (FileNotFoundException e) {
			// This were fine if it could happen
			assert (true);
		}

		// path to nonexistant file
		Path p = Paths.get("./NoSuchFile.sql");
		try {
			reader = new SQLScriptReader(p);
			fail();
		} catch (FileNotFoundException e) {
			assert (true);
		}

		// existing file
		p = Paths.get("./bin/script1.sql");
		try {
			reader = new SQLScriptReader(p);
			assert (true);
		} catch (FileNotFoundException e) {
			fail();
		}
	}

	@Test
	public void parse() {
		SQLScriptReader reader;
		Path p;

		// empty file
		p = Paths.get("./test/script1.sql");
		try {
			reader = new SQLScriptReader(p);
			assertFalse(reader.hasNextQuery());
			assertFalse(reader.hasNextQuery());
		} catch (FileNotFoundException e) {
			fail();
		}

		// script without comments
		p = Paths.get("./test/script2.sql");
		try {
			reader = new SQLScriptReader(p);
			int count = 0;
			while (reader.hasNextQuery()) {
				assertTrue(reader.hasNextQuery());
				Query q = reader.nextQuery();
				assertNotNull(q);
				System.out.print(q.getLineNumber() + "" + ':');
				System.out.println(q.getQuery());
				count++;
			}
			assertEquals("Wrong Query Count", 5, count);
		} catch (FileNotFoundException e) {
			fail();
		}

		// script with comments everywhere
		p = Paths.get("./test/script3.sql");
		try {
			reader = new SQLScriptReader(p);
			int count = 0;
			while (reader.hasNextQuery()) {
				assertTrue(reader.hasNextQuery());
				Query q = reader.nextQuery();
				assertNotNull(q);
				System.out.print(q.getLineNumber() + "" + ':');
				System.out.println(q.getQuery());
				count++;
			}
			assertEquals("Wrong Query Count", 4, count);
		} catch (FileNotFoundException e) {
			fail();
		}
	}
}
