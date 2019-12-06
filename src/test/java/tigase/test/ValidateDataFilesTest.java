/*
 * Tigase Test Suite - Tigase Test Suite - automated testing framework for Tigase Jabber/XMPP Server.
 * Copyright (C) 2005 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import tigase.test.util.ScriptFileLoader;
import tigase.test.util.ScriptFileLoader.StanzaEntry;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author andrzej
 */
public class ValidateDataFilesTest {
	
	@org.junit.Test
	public void testValidation() throws IOException {
		Queue<StanzaEntry> parsed = new ArrayDeque<>();
		Map<String,String> replaces = new HashMap<>();
		File dir = new File("tests/data");
		for (File f : dir.listFiles()) {
			if (!f.getName().endsWith(".cot"))
				continue;
			
			ScriptFileLoader loader = new ScriptFileLoader(f.getAbsolutePath(), parsed, replaces);
			loader.loadSourceFile();
		}
		assertTrue(true);
	}
	
}
