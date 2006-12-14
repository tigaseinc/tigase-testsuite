/*  Package Jabber Server
 *  Copyright (C) 2001, 2002, 2003, 2004, 2005
 *  "Artur Hefczyc" <kobit@users.sourceforge.net>
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

import tigase.test.TestAbstract;
import tigase.test.util.Params;
import javax.management.Attribute;
import tigase.test.ResultsDontMatchException;
import tigase.xml.Element;
import tigase.test.util.ElementUtil;

import static tigase.util.JID.*;


/**
 * Describe class TestPrivacyList here.
 *
 *
 * Created: Tue Dec 12 18:07:02 2006
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestPrivacyList extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String user_emil = "test_user@localhost";
  private String hostname = "localhost";
  private String jid = null;
  private String id = null;
	private String xmlns = "jabber:iq:privacy";

  private String[] elems =
	{"iq", "iq", "iq", "iq", "iq", "iq", "iq", "iq", "iq"
	 , "iq", "iq", "iq", "iq", "iq"};
  private int counter = 0;

  private Element expected_query = null;
  private Attribute[] result = null;
  private Attribute[] result_2 = null;
  private String[] resp_name = null;
  private int res_cnt = 0;

	/**
	 * Creates a new <code>TestPrivacyList</code> instance.
	 *
	 */
	public TestPrivacyList() {
    super(
      new String[] {"jabber:client"},
      new String[] {"privacy-lists"},
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
				if (ElementUtil.equalElemsDeep(expected_query, element)
					&& ElementUtil.equalElemsDeep(element, expected_query)) {
					error = false;
				}
      } else {
				if (expected_query == null) {
					error = false;
				} // end of if (expected_query == null)
			} // end of else
      if (error) {
        throw new ResultsDontMatchException(
          "Expected: " + expected_query + ", Received: " + element.toString());
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
      new Attribute("id", "privacy_" + counter),
      new Attribute("to", jid),
    };
    res_cnt = 0;
    switch (counter) {
    case 1:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_1"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_1\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\"/>"
        + "</iq>";
    case 2:
			expected_query = new Element("iq",
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_2"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"set\" id=\"privacy_2\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\">"
				+ "<list name='public'>"
				+ "<item type='jid'	value='tybalt@example.com' action='deny' order='3'/>"
        + "<item type='jid' value='paris@example.org' action='deny' order='5'/>"
        + "<item action='allow' order='68'/>"
				+ "</list>"
        + "</query>"
        + "</iq>";
    case 3:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new Element[] {
							new Element("list",
								new String[] {"name"},
								new String[] {"public"})},
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_3"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_3\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\"/>"
        + "</iq>";
    case 4:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new Element[] {
							new Element("list",
								new Element[] {
									new Element("item",
										new String[] {"type", "value", "action", "order"},
										new String[] {"jid", "tybalt@example.com", "deny", "3"}),
									new Element("item",
										new String[] {"type", "value", "action", "order"},
										new String[] {"jid", "paris@example.org", "deny", "5"}),
									new Element("item",
										new String[] {"action", "order"},
										new String[] {"allow", "68"})
								},
								new String[] {"name"},
								new String[] {"public"})},
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_4"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_4\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\">"
				+ "<list name=\"public\"/>"
				+ "</query>"
        + "</iq>";
    case 5:
			expected_query = new Element("iq",
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_5"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"set\" id=\"privacy_5\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\">"
				+ "<active name=\"public\"/>"
				+ "</query>"
        + "</iq>";
    case 6:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new Element[] {
							new Element("active",
								new String[] {"name"},
								new String[] {"public"}),
							new Element("list",
								new String[] {"name"},
								new String[] {"public"})},
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_6"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_6\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\"/>"
        + "</iq>";
    case 7:
			expected_query = new Element("iq",
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_7"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"set\" id=\"privacy_7\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\">"
				+ "<default name=\"public\"/>"
				+ "</query>"
        + "</iq>";
    case 8:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new Element[] {
							new Element("default",
								new String[] {"name"},
								new String[] {"public"}),
							new Element("active",
								new String[] {"name"},
								new String[] {"public"}),
							new Element("list",
								new String[] {"name"},
								new String[] {"public"})},
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_8"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_8\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\"/>"
        + "</iq>";
    case 9:
			expected_query = new Element("iq",
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_9"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"set\" id=\"privacy_9\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\">"
				+ "<default/>"
				+ "</query>"
        + "</iq>";
    case 10:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new Element[] {
							new Element("active",
								new String[] {"name"},
								new String[] {"public"}),
							new Element("list",
								new String[] {"name"},
								new String[] {"public"})},
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_10"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_10\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\"/>"
        + "</iq>";
    case 11:
			expected_query = new Element("iq",
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_11"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"set\" id=\"privacy_11\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\">"
				+ "<active/>"
				+ "</query>"
        + "</iq>";
    case 12:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new Element[] {
							new Element("list",
								new String[] {"name"},
								new String[] {"public"})},
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_12"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_12\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\"/>"
        + "</iq>";
    case 13:
			expected_query = new Element("iq",
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_13"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"set\" id=\"privacy_13\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\">"
				+ "<list name=\"public\"/>"
				+ "</query>"
        + "</iq>";
    case 14:
			expected_query = new Element("iq",
				new Element[] {new Element("query",
						new String[] {"xmlns"},
						new String[] {xmlns})},
				new String[] {"to", "type", "id"},
				new String[] {jid, "result", "privacy_14"});
      resp_name = new String[] {"iq"};
      return
        "<iq type=\"get\" id=\"privacy_14\" from=\"" + jid + "\">"
        + "<query xmlns=\"" + xmlns + "\"/>"
        + "</iq>";
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
    ++res_cnt;
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

} // TestPrivacyList
