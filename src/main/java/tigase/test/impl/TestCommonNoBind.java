/**
 * Tigase Test Suite - Automated testing framework for Tigase Jabber/XMPP Server.
 * Copyright (C) 2005 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.test.impl;

/**
 * Class TestCommonNoBind.java is responsible for 
 *
 * <p>
 * Created: Mon Apr 23 08:47:37 2007
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version 1.0.0
 */

public class TestCommonNoBind extends TestCommon {

	/**
	 * Creates a new <code>TestCommonNoBind</code> instance.
	 *
	 */
	public TestCommonNoBind() {
    super(
      new String[] {"jabber:client"},
      new String[] {"common-no-bind"},
      new String[] {"stream-open"},
      new String[] {"tls-init", "auth-sasl", "xmpp-bind"}
      );
	}

}// TestCommonNoBind
