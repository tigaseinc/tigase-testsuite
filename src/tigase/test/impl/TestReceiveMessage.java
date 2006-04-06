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
import java.net.SocketTimeoutException;
import java.util.Queue;
import javax.management.Attribute;
import tigase.test.ResultCode;
import tigase.test.TestAbstract;
import tigase.test.util.Params;
import tigase.test.util.SocketXMLIO;
import tigase.test.util.TestUtil;
import tigase.xml.Element;

import static tigase.util.JID.*;

/**
 * Describe class TestReceiveMessage here.
 *
 *
 * Created: Wed Jun  8 08:57:57 2005
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestReceiveMessage extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String hostname = "localhost";
  private String jid = null;
  private String msg_1 = null;
  private String msg_2 = null;

  private String data = null;
  private Attribute[] resp_attribs = null;

  /**
   * Creates a new <code>TestReceiveMessage</code> instance.
   *
   */
  public TestReceiveMessage() {
    super(
      new String[] {"jabber:client"},
      new String[] {"msg-listen"},
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
    if (element == null) {
      //      System.out.println("JID = " + jid);
      TestUtil.addDaemonJID(jid, (Socket)params.get("socket"));
      data = null;
      return "";
    } // end of if (element == null)
    data = msg_1 + element.getAttribute("from") + msg_2;
    return "message";
  }

  /**
   * Describe <code>getElementData</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String</code> value
   * @exception Exception if an error occurs
   */
  public String getElementData(final String string) throws Exception {
    return data;
  }

  /**
   * Describe <code>getRespElementNames</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String[]</code> value
   * @exception Exception if an error occurs
   */
  public String[] getRespElementNames(final String string) throws Exception {
    return new String[] {"message"};
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
  public Attribute[] getRespElementAttributes(final String string)
    throws Exception {
    return resp_attribs;
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
    hostname = params.get("-host", hostname);
    user_resr = params.get("-user_resr", user_resr);
    String name = getNodeNick(user_name);
    if (name == null || name.equals("")) {
      jid = user_name + "@" + hostname + "/" + user_resr;
    } else {
      jid = user_name + "/" + user_resr;
    } // end of else
    //    resp_attribs = new Attribute[] {new Attribute("to", jid)};
    msg_1 = "<message from='" + jid + "' to='";
    msg_2 = "'>Message OK</message>";
  }

  /**
   * Describe <code>run</code> method here.
   *
   * @return a <code>boolean</code> value
   */
  public boolean run() {
    try {
      String elem = null;
      SocketXMLIO io = (SocketXMLIO)params.get("socketxmlio");
      if (io == null) {
        resultCode = ResultCode.SOCKET_NOT_INITALIZED;
        return false;
      } // end of if (sock == null)
      elem = nextElementName(reply);
      while (elem != null) {
        debug("Processing element: " + elem + "\n");
        Queue<Element> results = io.read();
        Element rep = null;
        while ((rep = results.poll()) != null) {
          reply = rep;
          debug("Response data: " + reply.toString() + "\n");
          elem = nextElementName(reply);
          String data = getElementData(elem);
          if (data != null && !data.equals("")) {
            debug("Element data: " + data + "\n");
            io.write(data);
          } // end of if (data != null && !data.equals(""))
        }
      }
      return true;
    } catch (SocketTimeoutException e) {
      addInput("" + e + "\n" + TestUtil.stack2String(e));
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      return false;
    } catch (Exception e) {
      addInput("" + e + "\n" + TestUtil.stack2String(e));
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      e.printStackTrace();
      return false;
    } // end of try-catch
  }

} // TestReceiveMessage
