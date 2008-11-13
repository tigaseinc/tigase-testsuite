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
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package tigase.test.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;

import tigase.xml.DomBuilderHandler;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.SingletonFactory;

public class ScriptFileLoader {

	public static enum Action {
		expect, send;
	}

	private enum ParserState {
		expect_stanza, send_stanza, start;
	}

	public static class StanzaEntry {

		private Action action = null;
		private Element[] stanza = null;

		public StanzaEntry(Action action, Element[] stanza) {
			this.action = action;
			this.stanza = stanza;
		}

		public Action getAction() {
			return action;
		}

		public Element[] getStanza() {
			return stanza;
		}

	}

	private static final SimpleParser parser = SingletonFactory.getParserInstance();

	private final String file;

	private final Map<String, String> replace;

	private final Queue<StanzaEntry> stanzas_buff;

	public ScriptFileLoader(String source_file, Queue<StanzaEntry> stanzas_buff2, Map<String, String> replaces) {
		this.file = source_file;
		this.stanzas_buff = stanzas_buff2;
		this.replace = replaces;
	}

	public void loadSourceFile() {
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
					break;
				case send_stanza:
				case expect_stanza:
					if (!line.equals("{") && !line.equals("}") && !line.startsWith("#")) {
						buff.append(line + '\n');
					}
					if (line.equals("}")) {
						Element[] elems = parseXMLData(buff.toString());
						if (elems != null) {
							switch (state) {
							case send_stanza:
								stanzas_buff.offer(new StanzaEntry(Action.send, elems));
								break;
							case expect_stanza:
								stanzas_buff.offer(new StanzaEntry(Action.expect, elems));
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
			buffr.close();
		} catch (IOException e) {
			throw new RuntimeException("Can't read source file: " + file, e);
		}
	}

	private Element[] parseXMLData(String data) {

		// Replace a few "variables"
		if (replace != null) {
			for (java.util.Map.Entry<String, String> entry : replace.entrySet()) {
				data = data.replace(entry.getKey(), entry.getValue());

			}
		}

		DomBuilderHandler domHandler = new DomBuilderHandler();
		parser.parse(domHandler, data.toCharArray(), 0, data.length());
		Queue<Element> elems = domHandler.getParsedElements();
		if (elems != null && elems.size() > 0) {
			return elems.toArray(new Element[elems.size()]);
		}
		return null;
	}
}
