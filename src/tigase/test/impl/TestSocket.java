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

import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.List;
import tigase.test.TestAbstract;
import tigase.test.ResultCode;
import javax.management.Attribute;
import tigase.xml.Element;
import tigase.test.util.Params;
import tigase.test.util.SocketXMLIO;

/**
 * Describe class TestScoket here.
 *
 *
 * Created: Tue May 17 20:19:38 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestSocket extends TestAbstract {

  private String hostname = null;
	private String serverip = null;
  private int port = 5222;
  private int socket_wait = 5000;

  /**
   * Creates a new <code>TestScoket</code> instance.
   *
   */
  public TestSocket() {
    super(
      new String[]
      {"jabber:client", "jabber:server", "jabber:component:accept"},
      new String[] {"socket"},
      null,
      null
      );
  }

  public String nextElementName(final Element reply) {
    return null;
  }

  public String getElementData(final String element) {
    return null;
  }

  public String[] getRespElementNames(final String element) {
    return null;
  }

  public Attribute[] getRespElementAttributes(final String element) {
    return null;
  }

  public String[] getRespOptionalNames(final String element) {
    return null;
  }

  // Implementation of TestIfc

  static long connectCounter = 0;
  static long connectAverTime = 0;

  /**
   * Describe <code>run</code> method here.
   *
   * @return a <code>boolean</code> value
   */
  public boolean run() {
    try {
      //      debug("socket create...\n");
      Socket client = new Socket();
      //      debug("socket setting reuse address to true...\n");
      client.setReuseAddress(true);
      //      debug("socket setting so timeout...\n");
      client.setSoTimeout(socket_wait);
      //      debug("socket connecting...\n");
      //      System.out.println("socket-wait = " + socket_wait);
      long timeStart = System.currentTimeMillis();
      client.connect(new InetSocketAddress(serverip, port), socket_wait);
      long timeEnd = System.currentTimeMillis();
      long connectTime = timeEnd - timeStart;
      connectAverTime = (connectAverTime + connectTime) / 2;
      if (++connectCounter % 10 == 0) {
        debug("Socket connect aver time: " + connectAverTime + " ms\n");
      } // end of if (timeEnd - timeStart > 1000)
      //      debug("socket done.");
      params.put("socket", client);
      params.put("socketxmlio", new SocketXMLIO(client));
      return true;
    } catch (SocketTimeoutException e) {
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      return false;
    } catch (Exception e) {
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      e.printStackTrace();
      return false;
    } // end of try-catch
  }

  /**
   * Describe <code>init</code> method here.
   *
   * @param map a <code>Map</code> value
   */
  public void init(final Params map) {
    super.init(map);
		serverip = map.get("-serverip", "127.0.0.1");
		hostname = map.get("-host", "localhost");
    port = map.get("-port", port);
    socket_wait = params.get("-socket-wait", socket_wait);
  }

} // TestScoket
