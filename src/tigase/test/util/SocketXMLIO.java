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
package tigase.test.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
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
public class SocketXMLIO {

  private static final int BUFF_SIZE = 16*1024;

  private Socket socket = null;
  private OutputStream out = null;
  private BufferedReader inp = null;
  private DomBuilderHandler dom = null;
  private SimpleParser parser = null;
  private char[] in_data = new char[BUFF_SIZE];

  /**
   * Creates a new <code>SocketXMLReader</code> instance.
   *
   */
  public SocketXMLIO(Socket sock) throws IOException {
    socket = sock;
    out = sock.getOutputStream();
    inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    dom = new DomBuilderHandler(new DefaultElementFactory());
    parser = new SimpleParser();
  }

  public void write(String data) throws IOException {
    if (!socket.isConnected()) {
      throw new ConnectException("Socket is not connected.");
    } // end of if (!socket.isConnected())
    //    System.out.println("OUTPUT: " + data);
    out.write(data.getBytes());
  }

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
// 			System.out.println("INPUT: " + new String(in_data, 0, res));
      parser.parse(dom, in_data, 0, res);
    } // end of if (res > 0)
    return dom.getParsedElements();
  }

} // SocketXMLIO
