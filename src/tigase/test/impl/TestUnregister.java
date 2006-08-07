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

import java.util.List;
import tigase.test.util.Params;
import tigase.test.TestAbstract;
import javax.management.Attribute;
import tigase.xml.Element;

import static tigase.util.JID.*;

/**
 * Describe class TestUnregister here.
 *
 *
 * Created: Sun May 22 11:05:15 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestUnregister extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String user_emil = "test_user@localhost";
  private String hostname = "localhost";
  private String jid = null;

  private String[] elems = {"iq"};
  private int counter = 0;

  /**
   * Creates a new <code>TestUnregister</code> instance.
   *
   */
  public TestUnregister() {
    super(
      new String[] {"jabber:client"},
      new String[] {"user-unregister"},
      new String[] {"socket", "stream-open", "auth"},
      new String[] {"user-register", "tls-init"}
      );
  }

  public String nextElementName(final Element reply) {
    if (counter < elems.length) {
      return elems[counter++];
    } // end of if (counter < elems.length)
    return null;
  }

  public String getElementData(final String element) throws Exception {
    switch (counter) {
    case 1:
      return "<iq type='set' id='unreg1' from='" + jid + "'>"
        + "<query xmlns='jabber:iq:register'>"
        + "<remove/>"
        + "</query>" +
        "</iq>";
    default:
      return null;
    } // end of switch (counter)
  }

  public String[] getRespElementNames(final String element) {
    return new String[] {"iq"};
  }

  public Attribute[] getRespElementAttributes(final String element) {
    switch (counter) {
    case 1:
      return new Attribute[]
      {
        new Attribute("type", "result"),
        new Attribute("id", "unreg1"),
        new Attribute("to", jid)
      };
    default:
      return null;
    } // end of switch (counter)
  }

  public String[] getRespOptionalNames(final String element) {
    return null;
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
  }

} // TestUnregister
