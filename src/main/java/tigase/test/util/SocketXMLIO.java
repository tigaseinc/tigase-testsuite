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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Queue;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.DefaultElementFactory;

/**
 * Describe class SocketXMLIO here.
 *
 *
 * Created: Wed May 18 16:23:47 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class SocketXMLIO implements XMLIO {

  private static final int BUFF_SIZE = 2*1024;

  protected Socket socket = null;
  private OutputStream out = null;
	//  private BufferedReader inp = null;
  private InputStreamReader inp = null;
  private DomBuilderHandler dom = null;
  private SimpleParser parser = null;
  private char[] in_data = new char[BUFF_SIZE];
	protected boolean ignore_presence = false;

  /**
   * Creates a new <code>SocketXMLReader</code> instance.
   *
   */
  public SocketXMLIO(Socket sock) throws IOException {
		setSocket(sock);
  }

	public void setSocket(Socket sock) throws IOException {
    socket = sock;
    out = sock.getOutputStream();
		//    inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    inp = new InputStreamReader(sock.getInputStream());
    dom = new DomBuilderHandler(new DefaultElementFactory());
    parser = new SimpleParser();
	}

	@Override
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {	}
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	/* (non-Javadoc)
	 * @see tigase.test.util.XMLIO#write(tigase.xml.Element)
	 */
	@Override
	public void write(Element data) throws IOException {
	  write(data.toString());
  }

  /* (non-Javadoc)
	 * @see tigase.test.util.XMLIO#write(java.lang.String)
	 */
	@Override
	public void write(String data) throws IOException {
    if (!socket.isConnected()) {
      throw new ConnectException("Socket is not connected.");
    } // end of if (!socket.isConnected())
    //System.out.println("OUTPUT: " + data);
    out.write(data.toString().getBytes());
		out.flush();
  }

  /* (non-Javadoc)
	 * @see tigase.test.util.XMLIO#read()
	 */
	@Override
	public Queue<Element> read() throws IOException {
    if (!socket.isConnected()) {
      throw new ConnectException("Socket is not connected.");
    } // end of if (!socket.isConnected())
    int res = inp.read(in_data);
    if (res < 0) {
      throw new EOFException("End of stream on network socket detected.");
    } // end of if (res < 0)
    if (res > 0) {
//       char[] tmp_data = new char[res];
//       for (int i = 0; i < tmp_data.length; i++) {
//         tmp_data[i] = in_data[i];
//       } // end of for (int i = 0; i < tmp_data.length; i++)
// 			System.out.println("\n [" + res + "] INPUT: " + new String(in_data, 0, res));
      parser.parse(dom, in_data, 0, res);
    } // end of if (res > 0)
		Queue<Element> results = dom.getParsedElements();
		if (ignore_presence && results != null) {
			Iterator<Element> it = results.iterator();
			while (it.hasNext()) {
				Element el = it.next();
				if (el.getName() == "presence") {
					it.remove();
				}
			}
		}
    return results;
  }

	@Override
	public void setIgnorePresence(boolean ignore) {
		ignore_presence = ignore;
	}

} // SocketXMLIO
