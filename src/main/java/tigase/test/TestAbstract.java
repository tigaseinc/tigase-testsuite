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
package tigase.test;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.Arrays;
import javax.management.Attribute;
import tigase.xml.Element;
import tigase.test.util.Params;
import tigase.test.util.TestUtil;
import tigase.test.util.XMLIO;

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
public abstract class TestAbstract extends TestEmpty {

  protected Params params = null;
  protected ResultCode resultCode = ResultCode.TEST_OK;
  protected Exception exception = null;
  protected Element reply = null;
	protected boolean timeoutOk = false;
	private boolean fullExceptionStack = false;
	private String error_message = "";

  /**
   * Creates a new <code>TestAbstract</code> instance.
   *
   */
  public TestAbstract(final String[] base_xmlns, final String[] implemented,
    final String[] depends, final String[] optional) {
		super(base_xmlns, implemented, depends, optional);
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

	public void release() {
		try {
			XMLIO io = (XMLIO)params.get("socketxmlio");
			io.close();
		} catch (Exception e) {	}
	}

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
        XMLIO io = (XMLIO)params.get("socketxmlio");
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
						if (reply.getName().equals("stream:features")) {
							processStreamFeatures(reply);
						}
            replyElement(reply);
            debug("Received: " + reply.toString() + "\n");
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
				addInput(getClass().getName() + ", " + e.getMessage());
				return false;
			} // end of if (timeoutOk) else
		} catch (ResultsDontMatchException e) {
			resultCode = ResultCode.PROCESSING_EXCEPTION;
			exception = e;
			addInput(getClass().getName() + ", " + e.getMessage());
			return false;
    } catch (Exception e) {
      addInput(getClass().getName() + ", " + e + "\n" + TestUtil.stack2String(e));
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
		error_message = getClass().getName() +
			", expected: '" + response + "', Received: '" + reply.toString() + "'";
    return false;
  }

	private void processStreamFeatures(Element features) {
		List<Element> children = features.getChildren("/stream:features/mechanisms");
		if (children != null && children.size() > 0) {
			Set<String> mechs = new HashSet<String>();
			for (Element child: children) {
				mechs.add(child.getCData());
			}
			params.put("features", mechs);
		}
	}

  /**
   * Describe <code>init</code> method here.
   *
   * @param params a <code>Params</code> value
   */
  public void init(final Params params) {
		super.init(params);
    this.params = params;
		timeoutOk = params.containsKey("-time-out-ok");
		fullExceptionStack = params.containsKey("-full-stack-trace");
//       && !params.containsKey("-on-one-socket");
//    collectHistory = true;
  }

//   public ResultCode getResult() {
//     return resultCode;
//   }

  /**
   * Describe <code>getResultCode</code> method here.
   *
   * @return an <code>int</code> value
   */
  public ResultCode getResultCode() {
    return resultCode;
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
				return getClass().getName() + ", " +
					resultCode.getMessage() + exception.toString() + "\n"
					+ stack2String(exception);
			} else {
				return getClass().getName() + ", " + exception.getMessage();
			}
    default:
      return getClass().getName() + ", " + resultCode.getMessage()
				+ ", "
				+ error_message;
    } // end of switch (resultCode)
  }

} // TestAbstract
