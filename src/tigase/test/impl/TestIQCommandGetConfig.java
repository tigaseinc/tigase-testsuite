/*  Package Jabber Server
 *  Copyright (C) 2001, 2002, 2003, 2004, 2005
 *  "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package tigase.test.impl;

import java.util.LinkedList;
import java.util.List;
import tigase.test.TestAbstract;
import tigase.test.StatItem;
import tigase.test.util.Params;
import javax.management.Attribute;
import tigase.test.ResultsDontMatchException;
import tigase.xml.Element;
import tigase.xml.XMLUtils;
import tigase.test.util.ElementUtil;

import static tigase.util.JID.*;

/**
 * Describe class TestIQCommandGetConfig here.
 *
 *
 * Created: Wed Jan 24 21:23:38 2007
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestIQCommandGetConfig extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String user_emil = "test_user@localhost";
  private String hostname = "localhost";
  private String jid = null;
  private String id = null;
	private String xmlns = "http://jabber.org/protocol/commands";

  private String[] elems = {"iq"};
  private int counter = 0;
  private Element expected_result = null;
  private Attribute[] result = null;
  private String[] resp_name = null;

	/**
	 * Creates a new <code>TestIQCommandGetConfig</code> instance.
	 *
	 */
	public TestIQCommandGetConfig() {
    super(
      new String[] {"jabber:client"},
      new String[] {"command-get-config"},
      new String[] {"socket", "stream-open", "auth"},
      new String[] {"user-register", "tls-init"}
      );
	}

  // Implementation of tigase.test.TestAbstract

  /**
   * Describe <code>nextElementName</code> method here.
   *
   * @param element an <code>Element</code> value
   * @return a <code>String</code> value
   * @exception Exception if an error occurs
   */
  public String nextElementName(final Element element) throws Exception {
    if (element != null) {
      boolean error = true;
      if (element != null) {
				if (ElementUtil.equalElemsDeep(expected_result, element)) {
					error = false;
					List<StatItem> stats = new LinkedList<StatItem>();
					List<Element> items = element.getChildren("/iq/command/x");
					for (Element item: items) {
						String name = item.getAttribute("var");
						int idx = name.indexOf("/");
						String comp = "unknown";
						String stat = name;
						if (idx >= 0) {
							comp = name.substring(0, idx);
							stat = name.substring(idx+1);
						}
						stats.add(new StatItem(comp,
								XMLUtils.unescape(item.getChildCData("/field/value")),
								"&nbsp;",	stat));
					} // end of for (Element item: items)
					params.put("Configuration", stats);
				}
      } else {
				if (expected_result == null) {
					error = false;
				} // end of if (expected_result == null)
			} // end of else
      if (error) {
        throw new ResultsDontMatchException(
          "Expected: " + expected_result + ", Received: " + element);
      } // end of if (error)
    } // end of if (element != null)
    if (counter < elems.length) {
      return elems[counter++];
    } // end of if (counter < elems.length)
    return null;
  }

  /**
   * Describe <code>getElementData</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String</code> value
   * @exception Exception if an error occurs
   */
  public String getElementData(final String string) throws Exception {
    result = new Attribute[] {
      new Attribute("type", "result"),
      new Attribute("id", "command_" + counter),
      new Attribute("to", jid),
    };
    switch (counter) {
    case 1:
			expected_result = new Element("iq",
				new Element[] {new Element("command",
						new Element[] {
							new Element("x",
								new String[] {"xmlns", "type"},
								new String[] {"jabber:x:data", "result"})},
						new String[] {"xmlns", "status", "node"},
						new String[] {xmlns, "completed", "list/session_1"})},
				new String[] {"type", "id", "from"},
				new String[] {"result", "command_1", "basic-conf." + hostname});
      resp_name = new String[] {"iq"};
      return
				"<iq type=\"set\" to=\"basic-conf." + hostname + "\" id=\"command_1\" >"
				+ "<command xmlns=\"http://jabber.org/protocol/commands\""
				+ "node=\"config/list/session_1\" /></iq>";
    default:
      return null;
    } // end of switch (counter)
  }

  /**
   * Describe <code>getRespElementNames</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String[]</code> value
   * @exception Exception if an error occurs
   */
  public String[] getRespElementNames(final String string) throws Exception {
    return resp_name;
  }

  /**
   * Describe <code>getRespOptionalNames</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String[]</code> value
   * @exception Exception if an error occurs
   */
  public String[] getRespOptionalNames(final String string) throws Exception {
    return null;
  }

  /**
   * Describe <code>getRespElementAttributes</code> method here.
   *
   * @param string a <code>String</code> value
   * @return an <code>Attribute[]</code> value
   * @exception Exception if an error occurs
   */
  public Attribute[] getRespElementAttributes(final String string) throws Exception {
		return result;
  }

  // Implementation of tigase.test.TestIfc

  /**
   * Describe <code>init</code> method here.
   *
   * @param params a <code>Params</code> value
   */
  public void init(final Params params) {
    super.init(params);
    user_name = params.get("-user-name", user_name);
    user_resr = params.get("-user_resr", user_resr);
    user_emil = params.get("-user-emil", user_emil);
    hostname = params.get("-host", hostname);
    String name = getNodeNick(user_name);
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
  }

}
