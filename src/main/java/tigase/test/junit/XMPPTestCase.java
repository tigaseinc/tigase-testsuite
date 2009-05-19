/*
 * Tigase XMPP/Jabber Test Suite
 * Copyright (C) 2004-2009 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev: $
 * Last modified by $Author: $
 * $Date: $
 */
package tigase.test.junit;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tigase.test.util.ElementUtil;
import tigase.test.util.EqualError;
import tigase.test.util.ScriptFileLoader;
import tigase.test.util.ScriptFileLoader.Action;
import tigase.test.util.ScriptFileLoader.StanzaEntry;
import tigase.xml.Element;

public abstract class XMPPTestCase {

	public static boolean isScriptValid(Collection<StanzaEntry> entries) {
		ScriptFileLoader.Action wantKind = ScriptFileLoader.Action.send;
		for (StanzaEntry scriptEntry : entries) {
			if (wantKind != null && scriptEntry.getAction() != wantKind) {
				throw new RuntimeException("Invalid script for JUnit test! Expected " + wantKind);
			} else if (wantKind == null && scriptEntry.getAction() == Action.send) {
				throw new RuntimeException("Invalid script for JUnit test! Expected not send element!");
			}
			if (scriptEntry.getAction() == Action.send
					&& (scriptEntry.getStanza() == null || scriptEntry.getStanza().length != 1)) {
				throw new RuntimeException("Invalid script for JUnit test! send may have only one element!");
			}
			switch (scriptEntry.getAction()) {
			case send:
				wantKind = null;
				break;
			default:
				wantKind = Action.send;
				break;
			}
		}
		return true;
	}

	private static EqualError equalsOneOf(Element[] expect, Collection<Element> data) {
		EqualError rs = null;
		if (expect == null && data == null) {
			return new EqualError(false, "Null expected");
		} else if (expect == null || data == null) {
			return new EqualError(false, "Result or expect data is null");
		} else {
			boolean result = false;
			for (Element d : data) {
				for (Element e : expect) {
					EqualError error = ElementUtil.equalElemsDeep(e, d);
					result |= error.equals;
					if (!error.equals) {
						rs = error;
					}
				}
			}
			return result ? null : rs;
		}
	}

	private static boolean equalsAll(Element[] expect, Collection<Element> data) {
		if (expect == null && data == null) {
			return true;
		} else if (expect == null || data == null) {
			return false;
		} else {
			boolean result = true;
			for (Element e : expect) {
				boolean found = false;
				for (Element d : data) {
					EqualError error = ElementUtil.equalElemsDeep(e, d);
					found |= error.equals;
				}
				result &= found;
				if(!found){
					System.out.println("EQ :: not found  "+e);
				}
			}
			return result;
		}
	}

	private static boolean equalsStrict(Element[] expect, Collection<Element> data) {
		if (expect == null && data == null) {
			return true;
		} else if (expect == null || data == null) {
			return false;
		} else if (expect.length != data.size()) {
			return false;
		} else {
			boolean result = true;
			Iterator<Element> dataI = data.iterator();
			int c = 0;
			while (dataI.hasNext()) {
				Element e = expect[c++];
				Element d = dataI.next();
				EqualError error = ElementUtil.equalElemsDeep(e, d);
				result &= error.equals;
			}
			return result;
		}
	}

	/**
	 * Method run test.
	 * 
	 * @param xmlio
	 */
	public static void test(final String configFile, final JUnitXMLIO xmlio) {
		try {
			if (configFile == null) {
				throw new RuntimeException("Script file must be specified.");
			}

			Queue<StanzaEntry> script = new LinkedList<StanzaEntry>();
			ScriptFileLoader scriptFileLoader = new ScriptFileLoader(configFile, script, null);
			scriptFileLoader.loadSourceFile();

			for (StanzaEntry scriptEntry : script) {
				Boolean ok = null;
				switch (scriptEntry.getAction()) {
				case send:
					Element stanza = scriptEntry.getStanza()[0];
					xmlio.read().clear();
					System.out.println(" >> " + stanza.toString());
					xmlio.write(stanza);
					break;
				case expect:
					Queue<Element> read = xmlio.read();
					for (Element element : read) {
						System.out.println(" << " + element.toString());
					}
					EqualError ee = equalsOneOf(scriptEntry.getStanza(), read);
					if (ee != null) {
						String error_message = (scriptEntry.getDescription() != null ? (scriptEntry.getDescription() + " :: ")
								: "")
								+ "expected:\n"
								+ Arrays.toString(scriptEntry.getStanza())
								+ "\nreceived: "
								+ Arrays.toString(xmlio.read().toArray(new Element[0])) + "\nDescription: " + ee.message;
						fail(error_message);
					}
					break;
				case expect_all:
					ok = equalsAll(scriptEntry.getStanza(), xmlio.read());
					if (!ok) {
						String error_message = "Expected all of: " + Arrays.toString(scriptEntry.getStanza()) + ", received: "
								+ Arrays.toString(xmlio.read().toArray(new Element[0]));
						fail(error_message);
					}
					break;
				case expect_strict:
					ok = equalsStrict(scriptEntry.getStanza(), xmlio.read());
					if (!ok) {
						String error_message = "Expected sequence: " + Arrays.toString(scriptEntry.getStanza())
								+ ", received: " + Arrays.toString(xmlio.read().toArray(new Element[0]));
						fail(error_message);
					}
					break;

				}
				if (ok != null && !ok) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("(^(.+)\\((.*)\\):.*)|(^(.+):.*)");
		Matcher m = pattern.matcher("send:{");
		boolean b = m.matches();

		if (b) {
			for (int i = 0; i <= m.groupCount(); i++) {
				System.out.println(i + ": " + m.group(i));
			}
		}

		String keyword = m.group(2) == null ? m.group(5) : m.group(2);
		String param = m.group(3) == null ? null : m.group(3);

		System.out.println(keyword + "(" + param + ")");

	}

	private String configFile;

	public XMPPTestCase() {
		this.configFile = null;
	}

	/**
	 * 
	 * @param sourceFile
	 *            file with test script
	 */
	public XMPPTestCase(String sourceFile) {
		this.configFile = sourceFile;
	}

	public void test(final JUnitXMLIO xmlio) {
		test(this.configFile, xmlio);
	}

}
