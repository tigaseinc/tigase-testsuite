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

//~--- non-JDK imports --------------------------------------------------------

import tigase.test.TestAbstract;
import tigase.test.util.Params;

import tigase.xml.Element;

import static tigase.util.JIDUtils.*;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Map;

import javax.management.Attribute;

//~--- classes ----------------------------------------------------------------

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
	private int counter = 0;
	private String[] elems = { "iq" };
	private String hostname = "localhost";
	private String jid = null;
	private String user_emil = "test_user@localhost";
	private String user_name = "test_user@localhost";
	private String user_resr = "xmpp-test";

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestUnregister</code> instance.
	 *
	 */
	public TestUnregister() {
		super(new String[] { "jabber:client" },
					new String[] { "user-unregister" },
					new String[] { "stream-open", "auth", "xmpp-bind" },
					new String[] { "user-register", "tls-init" });
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	public String getElementData(final String element) throws Exception {
		switch (counter) {
			case 1 :
				return "<iq type='set' id='unreg1' from='" + jid + "'>"
							 + "<query xmlns='jabber:iq:register'>" + "<remove/>" + "</query>"
							 + "</iq>";

			default :
				return null;
		}    // end of switch (counter)
	}

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 */
	public Attribute[] getRespElementAttributes(final String element) {
		if (element.equals("iq")) {
			return new Attribute[] {
				new Attribute("type", "result"), new Attribute("id", "unreg1")

				// , new Attribute("to", jid)
			};
		}

		return null;
	}

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 */
	public String[] getRespElementNames(final String element) {
		return new String[] { "iq" };
	}

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 */
	public String[] getRespOptionalNames(final String element) {
		return new String[] { "stream:features" };
	}

	//~--- methods --------------------------------------------------------------

	// Implementation of tigase.test.TestIfc

	/**
	 * Describe <code>init</code> method here.
	 *
	 * @param params a <code>Params</code> value
	 * @param vars
	 */
	public void init(final Params params, Map<String, String> vars) {
		super.init(params, vars);
		user_name = params.get("-user-name", user_name);
		user_resr = params.get("-user-resr", user_resr);
		user_emil = params.get("-user-emil", user_emil);
		hostname = params.get("-host", hostname);

		String name = getNodeNick(user_name);

		if ((name == null) || name.equals("")) {
			jid = user_name + "@" + hostname + "/" + user_resr;
		} else {
			jid = user_name + "/" + user_resr;
		}    // end of else
	}

	/**
	 * Method description
	 *
	 *
	 * @param reply
	 *
	 * @return
	 */
	public String nextElementName(final Element reply) {
		if (counter < elems.length) {
			return elems[counter++];
		}    // end of if (counter < elems.length)

		return null;
	}
}    // TestUnregister


//~ Formatted in Sun Code Convention


//~ Formatted by Jindent --- http://www.jindent.com
