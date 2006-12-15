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

import javax.management.Attribute;
import tigase.test.TestAbstract;
import tigase.xml.Element;
import tigase.test.util.Params;

/**
 * Describe class TestXMPPBind here.
 *
 *
 * Created: Thu May 26 14:24:00 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestXMPPBind extends TestAbstract {

  private String user_resr = "xmpp-test";
  private String[] elems = {"iq"};
  private int counter = 0;

  /**
   * Creates a new <code>TestXMPPBind</code> instance.
   *
   */
  public TestXMPPBind() {
    super(
      new String[] {"jabber:client"},
      new String[] {"xmpp-bind"},
      new String[] {"socket", "stream-open", "tls-init", "auth-sasl"},
      new String[] {"user-register"}
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
    switch (counter) {
    case 1:
      return
        "<iq type='set' id='bind1'>"
        + "<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>"
        + "<resource>" + user_resr + "</resource>"
        + "</bind>" +
        "</iq>";
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
    return new String[] {"iq"};
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
    switch (counter) {
    case 1:
      return new Attribute[]
      {
        new Attribute("type", "result"),
        new Attribute("id", "bind1")
      };
    default:
      return null;
    } // end of switch (counter)
  }

  // Implementation of TestIfc

  /**
   * Describe <code>init</code> method here.
   *
   * @param map a <code>Map</code> value
   */
  public void init(final Params map) {
    super.init(map);
    user_resr = params.get("-user_resr", user_resr);
  }

} // TestXMPPBind
