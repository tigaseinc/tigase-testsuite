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
package tigase.test.impl;

import java.util.Map;
import javax.management.Attribute;
import tigase.util.Algorithms;
import tigase.test.util.Params;
import tigase.test.TestAbstract;
import tigase.xml.Element;

/**
 * Describe class TestExtCompOpen here.
 *
 *
 * Created: Tue May 17 20:23:58 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestExtCompOpen extends TestAbstract {

  protected String hostname = "localhost";

  private String[] elems = {"stream:stream", "handshake"};
  private int counter = 0;
	private String conn_id = null;
	private String secret = "someSecret";

  /**
   * Creates a new <code>TestExtCompOpen</code> instance.
   *
   */
  public TestExtCompOpen() {
    super(
      new String[] {"jabber:component:connect"},
      new String[] {"stream-ext-comp"},
      new String[] {"socket"},
      null
      );
  }

	@Override
  public String nextElementName(final Element reply) {
    if (counter < elems.length) {
      return elems[counter++];
    } // end of if (counter < elems.length)
    return null;
  }

	@Override
  public void replyElement(final Element reply) throws Exception {
    if (reply != null && reply.getName().equals("stream:stream")) {
			conn_id = reply.getAttribute("id");
    }
  }

	@Override
  public String getElementData(final String element) throws Exception {
    if (element.equals("stream:stream")) {
      return "<stream:stream "
        + "xmlns='jabber:component:accept' "
        + "xmlns:stream='http://etherx.jabber.org/streams' "
        + "to='" + hostname + "'> ";
		}
    if (element.equals("handshake")) {
			String digest = Algorithms.hexDigest(conn_id, secret, "SHA");
			return "<handshake>" + digest + "</handshake>";
		}
    return null;
  }

	@Override
  public String[] getRespElementNames(final String element) {
    if (element.equals("stream:stream")) {
      return new String[] {"stream:stream"};
    }
    if (element.equals("handshake")) {
      return new String[] {"handshake"};
    }
    return null;
  }

	@Override
  public Attribute[] getRespElementAttributes(final String element) {
    if (element.equals("stream:stream")) {
      return new Attribute[]
      {
        new Attribute("xmlns", "jabber:component:accept"),
        new Attribute("xmlns:stream", "http://etherx.jabber.org/streams"),
        new Attribute("from", hostname)
      };
    }
    return null;
  }

	@Override
  public String[] getRespOptionalNames(final String element) {
    return null;
  }

  // Implementation of TestIfc

  /**
   * Describe <code>init</code> method here.
   *
   * @param map a <code>Map</code> value
   */
	@Override
  public void init(final Params map, Map<String, String> vars) {
    super.init(map, vars);
    hostname = params.get("-host", hostname);
		secret = params.get("-secret", secret);
  }

} // TestExtCompOpen
