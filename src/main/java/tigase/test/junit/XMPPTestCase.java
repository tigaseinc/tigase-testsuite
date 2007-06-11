package tigase.test.junit;

import static org.junit.Assert.fail;
import static tigase.test.junit.ScriptEntryKind.send;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import tigase.test.util.ElementUtil;
import tigase.xml.Element;

public abstract class XMPPTestCase {

	public static boolean isScriptValid(Collection<ScriptEntry> entries) {
		ScriptEntryKind wantKind = ScriptEntryKind.send;
		for (ScriptEntry scriptEntry : entries) {
			if (wantKind != null && scriptEntry.getKind() != wantKind) {
				throw new RuntimeException("Invalid script for JUnit test! Expected " + wantKind);
			} else if (wantKind == null && scriptEntry.getKind() == send) {
				throw new RuntimeException("Invalid script for JUnit test! Expected not send element!");
			}
			if (scriptEntry.getKind() == ScriptEntryKind.send
					&& (scriptEntry.getStanza() == null || scriptEntry.getStanza().length != 1)) {
				throw new RuntimeException("Invalid script for JUnit test! send may have only one element!");
			}
			switch (scriptEntry.getKind()) {
			case send:
				wantKind = null;
				break;
			default:
				wantKind = send;
				break;
			}
		}
		return true;
	}

	private static boolean equalsOneOf(Element[] expect, Collection<Element> data) {
		if (expect == null && data == null) {
			return true;
		} else if (expect == null || data == null) {
			return false;
		} else {
			boolean result = false;
			for (Element d : data) {
				for (Element e : expect) {
					result |= ElementUtil.equalElemsDeep(e, d);
				}
			}
			return result;
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
					found |= ElementUtil.equalElemsDeep(e, d);
				}
				result &= found;
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
				result &= ElementUtil.equalElemsDeep(e, d);
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

			Queue<ScriptEntry> script = TestHelper.loadSourceFile(configFile);
			isScriptValid(script);

			for (ScriptEntry scriptEntry : script) {
				Boolean ok = null;
				switch (scriptEntry.getKind()) {
				case send:
					xmlio.read().clear();
					xmlio.write(scriptEntry.getStanza()[0]);
					break;
				case expect:
					ok = equalsOneOf(scriptEntry.getStanza(), xmlio.read());
					if (!ok) {
						String error_message = "Expected one of: " + Arrays.toString(scriptEntry.getStanza())
								+ ", received: " + Arrays.toString(xmlio.read().toArray(new Element[0]));
						fail(error_message);
					}
					break;
				case expect_all:
					ok = equalsAll(scriptEntry.getStanza(), xmlio.read());
					if (!ok) {
						String error_message = "Expected all of: " + Arrays.toString(scriptEntry.getStanza())
								+ ", received: " + Arrays.toString(xmlio.read().toArray(new Element[0]));
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
				default:
					break;
				}
				if (ok != null && !ok) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void main(String[] args) {
		test("processPresence-empty.cor", null);
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
