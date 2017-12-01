/*
 * Tigase XMPP/Jabber Test Suite
 * Copyright (C) 2004-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation,  either version 3 of the
 * License, or (at your option) any later version.
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
package tigase.test;

/**
 * Describe class HistoryEntry here.
 *
 *
 * Created: Wed May 18 06:58:56 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class HistoryEntry {

	private String name = null;
	private Direction direction = null;
  private String content = null;

  /**
   * Creates a new <code>HistoryEntry</code> instance.
   *
   */
  public HistoryEntry(Direction dir, String cont, String testName) {
    direction = dir;
    content = cont;
		name = testName;
  }

  public Direction getDirection() {
    return direction;
  }

  public String getContent() {
    return content;
  }

	@Override
  public String toString() {
    return "<" + direction + " test='" + name + "'>\n" +
						content + "\n</" + direction + ">";
  }

} // HistoryEntry