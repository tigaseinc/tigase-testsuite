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

import java.util.Map;
import java.util.TreeMap;
import java.net.Socket;
import javax.management.Attribute;
import tigase.test.TestAbstract;
import tigase.test.util.Params;
import tigase.xml.Element;

import static tigase.util.JID.*;

/**
 * Describe class TestIQVersion here.
 *
 *
 * Created: Thu Jun 16 05:58:19 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestIQVersion extends TestAbstract {

  private String hostname = "localhost";
  private String from = null;
  private String[] elems = {"iq"};
  private int counter = 0;

  /**
   * Creates a new <code>TestIQVersion</code> instance.
   *
   */
  public TestIQVersion() {
    super(
      new String[] {"jabber:client"},
      new String[] {"iq-version"},
      new String[] {"socket", "stream-open"},
      new String[] {"stream-open", "auth", "user-register", "tls-init"}
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
    Map<String, String> ver = new TreeMap<String, String>();
    ver.put("Name", element.getCData("/iq/query/name"));
    ver.put("Version", element.getCData("/iq/query/version"));
    ver.put("OS", element.getCData("/iq/query/os"));
    Socket socket = (Socket)params.get("socket");
    String remote = socket.getInetAddress().getHostAddress();
    String local = socket.getLocalAddress().getHostAddress();
    ver.put("Remote IP", remote);
    ver.put("Local IP", local);
    params.put("Version", ver);
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
        "<iq type='get' to='" + hostname
        + "' from='" + from + "' id='version_1'>"
        + "<query xmlns='jabber:iq:version'/>" +
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
        new Attribute("id", "version_1")
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
    hostname = params.get("-host", hostname);
    String user_name = params.get("-user-name", "test_user@localhost");
    String user_resr = params.get("-user_resr", "xmpp-test");
    String name = getNodeNick(user_name);
    if (name == null || name.equals("")) {
      from = user_name + "@" + hostname + "/" + user_resr;
    } else {
      from = user_name + "/" + user_resr;
    } // end of else
  }

} // TestIQVersion
