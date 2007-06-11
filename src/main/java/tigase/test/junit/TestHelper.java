package tigase.test.junit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import tigase.xml.DomBuilderHandler;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.SingletonFactory;

public class TestHelper {

	private enum ParserState {
		expect_strict, expect_all_stanza, expect_stanza, send_stanza, start;
	}

	private static final SimpleParser parser = SingletonFactory.getParserInstance();

	public static Queue<ScriptEntry> loadSourceFile(String file) {
		Queue<ScriptEntry> stanzas = new LinkedList<ScriptEntry>();
		try {
			ParserState state = ParserState.start;
			StringBuilder buff = null;
			BufferedReader buffr = new BufferedReader(new FileReader(file));
			String line = buffr.readLine();
			while (line != null) {
				line = line.trim();
				switch (state) {
				case start:
					if (line.toLowerCase().startsWith("send:")) {
						state = ParserState.send_stanza;
						buff = new StringBuilder();
					}
					if (line.toLowerCase().startsWith("expect:")) {
						state = ParserState.expect_stanza;
						buff = new StringBuilder();
					}
					if (line.toLowerCase().startsWith("expect all:")) {
						state = ParserState.expect_all_stanza;
						buff = new StringBuilder();
					}
					if (line.toLowerCase().startsWith("expect strict:")) {
						state = ParserState.expect_strict;
						buff = new StringBuilder();
					}
					break;
				case send_stanza:
				case expect_stanza:
				case expect_strict:
				case expect_all_stanza:
					if (!line.equals("{") && !line.equals("}") && !line.startsWith("#")) {
						buff.append(line + '\n');
					}
					if (line.equals("}")) {
						Element[] elems = parseXMLData(buff.toString());
						if (elems != null) {
							switch (state) {
							case send_stanza:
								stanzas.offer(new ScriptEntry(ScriptEntryKind.send, elems));
								break;
							case expect_stanza:
								stanzas.offer(new ScriptEntry(ScriptEntryKind.expect, elems));
								break;
							case expect_strict:
								stanzas.offer(new ScriptEntry(ScriptEntryKind.expect_strict, elems));
								break;
							case expect_all_stanza:
								stanzas.offer(new ScriptEntry(ScriptEntryKind.expect_all, elems));
								break;
							default:
								break;
							}
						}
						state = ParserState.start;
					}
					break;
				default:
					break;
				}
				line = buffr.readLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Can't read source file: " + file, e);
		}
		return stanzas;
	}

	private static Element[] parseXMLData(String data) {

		DomBuilderHandler domHandler = new DomBuilderHandler();
		parser.parse(domHandler, data.toCharArray(), 0, data.length());
		Queue<Element> elems = domHandler.getParsedElements();
		if (elems != null && elems.size() > 0) {
			return elems.toArray(new Element[elems.size()]);
		}
		return null;
	}
}
