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
package tigase.test;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Arrays;
import javax.management.Attribute;
import tigase.xml.Element;
import tigase.test.util.SocketXMLIO;
import tigase.test.util.Params;
import tigase.test.util.TestUtil;

import static tigase.test.util.TestUtil.*;

/**
 * Describe class TestAbstract here.
 *
 *
 * Created: Sun May 22 11:08:23 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class TestAbstract implements TestIfc {

  private String[] BASE_XMLNS = null;
  private String[] IMPLEMENTED = null;
  private String[] DEPENDS = null;
  private String[] OPTIONAL = null;

  protected Params params = null;
  protected ResultCode resultCode = ResultCode.TEST_OK;
  protected Exception exception = null;
  protected Element reply = null;
  private List<HistoryEntry> history = null;
  private boolean deb = false;
  private boolean collectHistory = true;
	protected boolean timeoutOk = false;
	private boolean fullExceptionStack = false;
	private String error_message = "";

  /**
   * Creates a new <code>TestAbstract</code> instance.
   *
   */
  public TestAbstract(final String[] base_xmlns, final String[] implemented,
    final String[] depends, final String[] optional) {
    BASE_XMLNS = base_xmlns;
    IMPLEMENTED = implemented;
    DEPENDS = depends;
    OPTIONAL = optional;
  }

  public static String substituteVars(final String data,
    final String[] vars, final String[] vals) {
    String result = data;
    for (int i = 0; i < vars.length; i++) {
      result = result.replace(vars[i], vals[i]);
    } // end of for (int i = 0; i < vars.length; i++)
    return result;
  }

  public static boolean hasAttributes(Element elem, Attribute[] attrs) {
    if (attrs != null) {
      for (Attribute attr : attrs) {
        String val = elem.getAttribute(attr.getName());
        if (val == null) {
          return false;
        } // end of if (val == null)
        else {
          if (!val.equals(attr.getValue())) {
            return false;
          } // end of if (!val.equals(attr.getValue()))
        } // end of if (val == null) else
      } // end of for ()
    } // end of if (attrs != null)
    return true;
  }

  public void debug(String msg) {
    if (deb) {
      System.out.print(msg);
      System.out.flush();
    } // end of if (debug)
  }

  public abstract String nextElementName(final Element reply) throws Exception;

  public abstract String getElementData(final String element) throws Exception;

  public abstract String[] getRespElementNames(final String element)
    throws Exception;

  public abstract String[] getRespOptionalNames(final String element)
    throws Exception;

  public abstract Attribute[] getRespElementAttributes(final String element)
    throws Exception;

  public void replyElement(final Element reply) throws Exception {}

  // Implementation of tigase.test.TestIfc

  /**
   * Describe <code>run</code> method here.
   *
   * @return a <code>boolean</code> value
   */
  public boolean run() {
    try {
      String elem = null;
      while ((elem = nextElementName(reply)) != null) {
        debug("Processing element: " + elem + "\n");
        SocketXMLIO io = (SocketXMLIO)params.get("socketxmlio");
        if (io == null) {
          resultCode = ResultCode.SOCKET_NOT_INITALIZED;
          return false;
        } // end of if (sock == null)
        String data = getElementData(elem);
        if (data != null && !data.equals("")) {
          debug("Element data: " + data + "\n");
          addOutput(data);
          io.write(data);
        } // end of if (data != null && !data.equals(""))
        String[] responses = getRespElementNames(elem);
        boolean[] resp_found = new boolean[responses.length];
        String[] optional_resp = getRespOptionalNames(elem);
        Arrays.fill(resp_found, false);
        int index = 0;
        while (!resp_found[resp_found.length - 1]) {
          Queue<Element> results = io.read();
          Element rep = null;
          while (index < resp_found.length
            && (rep = results.poll()) != null) {
            reply = rep;
            replyElement(reply);
            debug("Response data: " + reply.toString() + "\n");
            addInput(reply.toString());
            resp_found[index] =
              checkResponse(reply, responses[index], optional_resp);
            if (!resp_found[index++]) {
              resultCode = ResultCode.RESULT_DOESNT_MATCH;
              return false;
            } // end of else
          }
          if (results.size() > 0) {
            reply = results.poll();
            resultCode = ResultCode.RESULT_DOESNT_MATCH;
            return false;
          } // end of if (index >= resp_found.length)
        } // end of while (!resp_found[resp_found-1])
      }
      return true;
    } catch (SocketTimeoutException e) {
			if (timeoutOk) {
				return true;
			}	else {
				resultCode = ResultCode.PROCESSING_EXCEPTION;
				exception = e;
				addInput(e.getMessage());
				return false;
			} // end of if (timeoutOk) else
		} catch (ResultsDontMatchException e) {
			resultCode = ResultCode.PROCESSING_EXCEPTION;
			exception = e;
			addInput(e.getMessage());
			return false;
    } catch (Exception e) {
      addInput("" + e + "\n" + TestUtil.stack2String(e));
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      e.printStackTrace();
      return false;
    } // end of try-catch
  }

  private boolean checkResponse(Element reply, String response,
    String[] optional_resp) throws Exception {
    if (reply.getName().equals(response)
      && hasAttributes(reply, getRespElementAttributes(response))) {
      return true;
    } else {
      if (optional_resp != null) {
        for (String opt : optional_resp) {
          if (reply.getName().equals(opt)
            && hasAttributes(reply, getRespElementAttributes(opt))) {
            return true;
          }
        } // end of for ()
      }
    } // end of else
		error_message =
			"Expected: " + response + ", Received: " + reply.toString();
    return false;
  }

  /**
   * Describe <code>init</code> method here.
   *
   * @param params a <code>Params</code> value
   */
  public void init(final Params params) {
    this.params = params;
    deb = params.containsKey("-debug");
    collectHistory = !params.containsKey("-daemon");
		timeoutOk = params.containsKey("-time-out-ok");
		fullExceptionStack = params.containsKey("-full-stack-trace");
//       && !params.containsKey("-on-one-socket");
//    collectHistory = true;
    if (collectHistory) {
      history = new LinkedList<HistoryEntry>();
    } // end of if (collectHistory)
  }

  /**
   * Describe <code>baseXMLNS</code> method here.
   *
   * @return a <code>String[]</code> value
   */
  public String[] baseXMLNS() {
    return BASE_XMLNS;
  }

  /**
   * Describe <code>implemented</code> method here.
   *
   * @return a <code>String[]</code> value
   */
  public String[] implemented() {
    return IMPLEMENTED;
  }

  /**
   * Describe <code>depends</code> method here.
   *
   * @return a <code>String[]</code> value
   */
  public String[] depends() {
    return DEPENDS;
  }

  /**
   * Describe <code>optional</code> method here.
   *
   * @return a <code>String[]</code> value
   */
  public String[] optional() {
    return OPTIONAL;
  }

  public ResultCode getResult() {
    return resultCode;
  }

  /**
   * Describe <code>getResultCode</code> method here.
   *
   * @return an <code>int</code> value
   */
  public int getResultCode() {
    return resultCode.ordinal();
  }

  public Element getLastResult() {
    return reply;
  }

  /**
   * Describe <code>getResultMessage</code> method here.
   *
   * @return a <code>String</code> value
   */
  public String getResultMessage() {
    switch (resultCode) {
    case PROCESSING_EXCEPTION:
			if (fullExceptionStack) {
				return resultCode.getMessage() + exception.toString() + "\n"
					+ stack2String(exception);
			} else {
				return exception.getMessage();
			}
    default:
      return resultCode.getMessage()
				+ ", "
				+ error_message;
    } // end of switch (resultCode)
  }

  /**
   * Describe <code>getHistory</code> method here.
   *
   * @return a <code>List</code> value
   */
  public List<HistoryEntry> getHistory() {
    return history;
  }

  public void addInput(String input) {
    if (collectHistory) {
      history.add(new HistoryEntry(Direction.INPUT, input));
    }
  }

  public void addOutput(String output) {
    if (collectHistory) {
      history.add(new HistoryEntry(Direction.OUTPUT, output));
    }
  }

} // TestAbstract
