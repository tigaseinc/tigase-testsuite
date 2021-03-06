/*
 * Tigase Test Suite - Tigase Test Suite - automated testing framework for Tigase Jabber/XMPP Server.
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
package tigase.test.util;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Describe interface OutputFilter here.
 *
 *
 * Created: Thu Jun  9 20:02:20 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public interface OutputFilter {

  void init(BufferedWriter out, String title, String description)
    throws IOException;

  void addContent(String content) throws IOException;

  void setColumnHeaders(String ... hd) throws IOException;

  void addRow(String ... cols) throws IOException;

  void close(String closingInfo) throws IOException;


} // OutputFilter