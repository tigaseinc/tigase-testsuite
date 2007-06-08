/*  Package Web Test Tools 
 *  Copyright (C) 2001 "Artur Hefczyc" <kobit@users.sourceforge.net>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * $Id: prj.el,v 1.5 2002/06/07 13:46:55 kobit Exp $
 * $Author$
 * $Date$
 */

package tigase.test.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import tigase.test.ResultCode;
import tigase.test.TestEmpty;
import tigase.test.util.ElementUtil;
import tigase.test.util.Params;
import tigase.test.util.TestUtil;
import tigase.test.util.XMLIO;
import tigase.util.JIDUtils;
import tigase.xml.DomBuilderHandler;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.SingletonFactory;

/**
 * Class TestCommon.java is responsible for 
 *
 * <p>
 * Created: Mon Apr 23 08:47:37 2007
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version 1.0.0
 */

public class TestCommon extends TestEmpty {

	private static final SimpleParser parser =
		SingletonFactory.getParserInstance();
  protected Params params = null;
  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String user_emil = "test_user@localhost";
  private String hostname = "localhost";
	private String source_file = "tests/data/sample-file.cot";
  private String jid = null;
  private String id = null;
  private String to = "test_user@localhost";
	protected boolean timeoutOk = false;
	private boolean fullExceptionStack = false;
  protected ResultCode resultCode = ResultCode.TEST_OK;
  protected Exception exception = null;
	private String error_message = "";
	private Queue<StanzaEntry> stanzas = null;

	/**
	 * Creates a new <code>TestCommon</code> instance.
	 *
	 */
	public TestCommon() {
    super(
      new String[] {"jabber:client"},
      new String[] {"common"},
      new String[] {"socket", "stream-open", "auth"},
      new String[] {"user-register", "tls-init"}
      );
	}

	/**
	 * Describe <code>run</code> method here.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean run() {
		try {
			StanzaEntry entry = null;
			while ((entry = stanzas.poll()) != null) {
        XMLIO io = (XMLIO)params.get("socketxmlio");
        if (io == null) {
          resultCode = ResultCode.SOCKET_NOT_INITALIZED;
          return false;
        } // end of if (sock == null)
				switch (entry.action) {
				case send:
					for (Element elem: entry.stanza) {
						debug("\nSending: " + elem.toString());
						addOutput(elem.toString());
						io.write(elem.toString());
					} // end of for (Element elem: stanza)
					break;
				case expect:
					boolean found = false;
					Queue<Element> all_results = new LinkedList<Element>();
					Queue<Element> results = null;
					while (results == null || results.size() == 0) {
						results = io.read();
					} // end of while (!found)
					all_results.addAll(results);
					for (int exp = 0; exp < entry.stanza.length && !found; ++exp) {
						Element received = null;
						while ((received = results.poll()) != null && !found) {
							addInput(received.toString());
							found = ElementUtil.equalElemsDeep(entry.stanza[exp], received);
						} // end of for (int res = 0; res < results.size() && !found; res++)
					} // end of for (Element expected: entry.stanza)
					if (!found) {
            resultCode = ResultCode.RESULT_DOESNT_MATCH;
						error_message = "Expected one of: " + Arrays.toString(entry.stanza)
							+ ", received: "
							+ Arrays.toString(all_results.toArray(new Element[0]));
            return false;
					} // end of if (!found)
					break;
				default:
					break;
				} // end of switch (entry.action)
			} // end of while ((entry = stanzas.poll()) != null)
			return true;
		} catch (SocketTimeoutException e) {
			if (timeoutOk) {
				return true;
			}	else {
				resultCode = ResultCode.PROCESSING_EXCEPTION;
				exception = e;
				addInput(getClass().getName() + ", " + e.getMessage());
				return false;
			} // end of if (timeoutOk) else
		} catch (Exception e) {
      addInput(getClass().getName() + ", " + e + "\n" + TestUtil.stack2String(e));
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      e.printStackTrace();
      return false;
		} // end of catch
	}

  /**
   * Describe <code>getResultCode</code> method here.
   *
   * @return an <code>int</code> value
   */
  public ResultCode getResultCode() {
    return resultCode;
  }

  public String getResultMessage() {
    switch (resultCode) {
    case PROCESSING_EXCEPTION:
			if (fullExceptionStack) {
				return getClass().getName() + ", " +
					resultCode.getMessage() + exception.toString() + "\n"
					+ TestUtil.stack2String(exception);
			} else {
				return getClass().getName() + ", " + exception.getMessage();
			}
    default:
      return resultCode.getMessage()
				+ ", "
				+ error_message;
    } // end of switch (resultCode)
  }

  public void init(final Params params) {
		super.init(params);
    this.params = params;
    user_name = params.get("-user-name", user_name);
    user_resr = params.get("-user_resr", user_resr);
    user_emil = params.get("-user-emil", user_emil);
    hostname = params.get("-host", hostname);
    String name = JIDUtils.getNodeNick(user_name);
    if (name == null || name.equals("")) {
      jid = user_name + "@" + hostname + "/" + user_resr;
    } else {
      jid = user_name + "/" + user_resr;
    } // end of else
    if (name == null || name.equals("")) {
      id = user_name + "@" + hostname;
    } else {
      id = user_name;
    } // end of else
    to = params.get("-to-jid", to);
		timeoutOk = params.containsKey("-time-out-ok");
		fullExceptionStack = params.containsKey("-full-stack-trace");
		source_file = params.get("-source-file", source_file);
		stanzas = new LinkedList<StanzaEntry>();
		loadSourceFile(source_file);
  }

	private enum ParserState {
		start, send_stanza, expect_stanza;
	}

	private void loadSourceFile(String file) {
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
					if (!line.equals("{")
						&& !line.equals("}")
						&& !line.startsWith("#")) {
						buff.append(line);
					}
					if (line.equals("}")) {
						Element[] elems = parseXMLData(buff.toString());
						if (elems != null) {
							switch (state) {
							case send_stanza:
								stanzas.offer(new StanzaEntry(Action.send, elems));
								break;
							case expect_stanza:
								stanzas.offer(new StanzaEntry(Action.expect, elems));
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
	}

	private Element[] parseXMLData(String data) {

		// Replace a few "variables"
		data = data.replace("$(from-jid)", jid);
		data = data.replace("$(to-jid)", to);
		data = data.replace("$(hostname)", hostname);
		data = data.replace("$(number)", "");
		data = data.replace("$(cdata)", "");

		DomBuilderHandler domHandler = new DomBuilderHandler();
		parser.parse(domHandler, data.toCharArray(), 0, data.length());
		Queue<Element> elems = domHandler.getParsedElements();
		if (elems != null && elems.size() > 0) {
			return elems.toArray(new Element[elems.size()]);
		}
		return null;
	}

	private class StanzaEntry {

		private Action action = null;
		private Element[] stanza = null;

		public StanzaEntry(Action action, Element[] stanza) {
			this.action = action;
			this.stanza = stanza;
		}

	}

	private enum Action {
		send, expect;
	}

}// TestCommon
