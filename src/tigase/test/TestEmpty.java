/*
 * Tigase Jabber/XMPP Test Suite
 * Copyright (C) 2004-2007 "Artur Hefczyc" <artur.hefczyc@tigase.org>
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

import java.util.LinkedList;
import java.util.List;
import tigase.test.util.Params;
import tigase.xml.Element;

/**
 * Describe class TestEmpty here.
 *
 *
 * Created: Mon Apr 23 17:25:02 2007
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class TestEmpty implements TestIfc {

  private String[] BASE_XMLNS = null;
  private String[] IMPLEMENTED = null;
  private String[] DEPENDS = null;
  private String[] OPTIONAL = null;
  private List<HistoryEntry> history = null;
  private boolean collectHistory = true;
  private boolean deb = false;

	/**
	 * Creates a new <code>TestEmpty</code> instance.
	 *
	 */
	public TestEmpty(final String[] base_xmlns, final String[] implemented,
    final String[] depends, final String[] optional) {
    BASE_XMLNS = base_xmlns;
    IMPLEMENTED = implemented;
    DEPENDS = depends;
    OPTIONAL = optional;
	}

	// Implementation of tigase.test.TestIfc

	/**
	 * Describe <code>run</code> method here.
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean run() {
		return false;
	}

	/**
	 * Describe <code>init</code> method here.
	 *
	 * @param params a <code>Params</code> value
	 */
	public void init(final Params params) {
    collectHistory = !params.containsKey("-daemon");
    if (collectHistory) {
      history = new LinkedList<HistoryEntry>();
    } // end of if (collectHistory)
    deb = params.containsKey("-debug");
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

	/**
	 * Describe <code>getResultCode</code> method here.
	 *
	 * @return an <code>int</code> value
	 */
	public ResultCode getResultCode() {
		return ResultCode.TEST_OK;
	}

	/**
	 * Describe <code>getResultMessage</code> method here.
	 *
	 * @return a <code>String</code> value
	 */
	public String getResultMessage() {
		return null;
	}

	/**
	 * Describe <code>getLastResult</code> method here.
	 *
	 * @return an <code>Element</code> value
	 */
	public Element getLastResult() {
		return null;
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

  public void debug(String msg) {
    if (deb) {
      System.out.print(msg);
      System.out.flush();
    } // end of if (debug)
  }

} // TestEmpty