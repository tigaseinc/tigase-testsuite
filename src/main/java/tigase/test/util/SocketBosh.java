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
package tigase.test.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.LinkedList;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.DefaultElementFactory;

/**
 * Describe class SocketBosh here.
 *
 *
 * Created: Wed May 18 16:23:47 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class SocketBosh extends SocketXMLIO {

  private static final int BUFF_SIZE = 2*1024;
	private long rid = 1216;
	private String sid = null;
	private String authId = null;
	private boolean restart = false;
	private boolean terminate = false;

	/**
   * Creates a new <code>SocketXMLReader</code> instance.
   *
   */
  public SocketBosh(Socket sock) throws IOException {
		super(sock);
  }

  /* (non-Javadoc)
	 * @see tigase.test.util.XMLIO#write(java.lang.String)
	 */
	public void write(String data) throws IOException {
 		//System.out.println("SocketBosh writing data: " + data);
		if (data != null && data.startsWith("<stream:stream")) {
			restart = true;
			initSocket(null);
		} else {
			initSocket(data);
		}
  }

  /* (non-Javadoc)
	 * @see tigase.test.util.XMLIO#read()
	 */
	public Queue<Element> read() throws IOException {
		Queue<Element> results = new LinkedList<Element>();
		try {
			Queue<Element> elements = super.read();
			if (elements != null) {
				results = new LinkedList<Element>();
				for (Element body: elements) {
					if (body.getName() == "body") {
						String temp = body.getAttribute("sid");
						if (temp != null) {
						sid = temp;
						}
						temp = body.getAttribute("authid");
						if (temp != null) {
							authId = temp;
						}
						if (body.getChildren() != null) {
							results.addAll(body.getChildren());
						}
					} else {
						results.offer(body);
					}
				}
			}
		} catch (EOFException e) {
			initSocket(null);
		} catch (SocketException e) {
			initSocket(null);
		}
		return results;
  }

	public void close() {
		//System.out.println("Closing Bosh socket.");
		//super.close();
		terminate = true;
		try {
			initSocket(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initSocket(String data) throws IOException {
		Socket client = new Socket();
		client.setReuseAddress(true);
		client.setSoTimeout(socket.getSoTimeout());
		client.setReceiveBufferSize(BUFF_SIZE);
		client.connect(socket.getRemoteSocketAddress(), socket.getSoTimeout());
		setSocket(client);
		if (data != null && data.startsWith("<body")) {
			super.write(data);
		} else {
			Element body = new Element("body", data,
				new String[] {"xmlns", "sid", "rid"},
				new String[] {"http://jabber.org/protocol/httpbind", sid, ""+(++rid)});
			// 		System.out.println("SocketBosh writing data: " + body_str);
			if (restart) {
				body.setAttribute("xmpp:restart", "true");
				restart = false;
			}
			if (terminate) {
				body.setAttribute("type", "terminate");
			}
			super.write(body.toString());
			if (terminate) {
				super.close();
			}
		}
	}

} // SocketBosh
