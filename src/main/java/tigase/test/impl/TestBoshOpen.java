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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.management.Attribute;
import tigase.test.HistoryEntry;
import tigase.test.util.Params;
import tigase.test.TestAbstract;
import tigase.test.util.SocketXMLIO;
import tigase.test.util.ElementUtil;
import tigase.xml.Element;

/**
 * Describe class TestBoshOpen here.
 *
 *
 * Created: Tue May 17 20:23:58 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestBoshOpen extends TestAbstract {

  protected String hostname = "localhost";

  private String[] elems = {"body"};
  private int counter = 0;

  /**
   * Creates a new <code>TestBoshOpen</code> instance.
   *
   */
  public TestBoshOpen() {
    super(
      new String[]
      {"jabber:client", "jabber:server", "jabber:component:accept"},
      new String[] {"stream-bosh"},
      new String[] {"socket-bosh"},
      null
      );
  }

  public String nextElementName(final Element reply) {
    if (counter < elems.length) {
      return elems[counter++];
    } // end of if (counter < elems.length)
    return null;
  }

  public void replyElement(final Element reply) throws Exception {
    if (reply != null && reply.getName().equals("stream:stream")) {
      params.put("session-id", reply.getAttribute("id"));
    }
  }

  public String getElementData(final String element) throws Exception {
    if (element.equals("body")) {
      return "<body "
        + "xmlns='http://jabber.org/protocol/httpbind' "
        + "xmlns:xmpp='urn:xmpp:xbosh' "
        + "xml:lang='en' "
        + "rid='1216' "
        + "content='text/xml; charset=utf-8' "
        + "to='" + hostname + "' "
        + "ver='1.6'/>";
    }
    return null;
  }

  public String[] getRespElementNames(final String element) {
    if (element.equals("body")) {
      return new String[] {"stream:features"};
    }
    return null;
  }

  public Attribute[] getRespElementAttributes(final String element) {
    if (element.equals("stream:features")) {
      return new Attribute[] { };
    }
    return null;
  }

  public String[] getRespOptionalNames(final String element) {
    return null;
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
  }

} // TestBoshOpen
