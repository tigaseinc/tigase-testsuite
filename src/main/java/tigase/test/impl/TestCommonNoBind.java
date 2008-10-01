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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import tigase.test.ResultCode;
import tigase.test.TestEmpty;
import tigase.test.util.ElementUtil;
import tigase.test.util.EqualError;
import tigase.test.util.Params;
import tigase.test.util.TestUtil;
import tigase.test.util.XMLIO;
import tigase.util.JIDUtils;
import tigase.xml.DomBuilderHandler;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.SingletonFactory;

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
