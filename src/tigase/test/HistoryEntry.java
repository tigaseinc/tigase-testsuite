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
package tigase.test;

/**
 * Describe class HistoryEntry here.
 *
 *
 * Created: Wed May 18 06:58:56 2005
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class HistoryEntry {

  private Direction direction = null;
  private String content = null;

  /**
   * Creates a new <code>HistoryEntry</code> instance.
   *
   */
  public HistoryEntry(Direction dir, String cont) {
    direction = dir;
    content = cont;
  }

  public Direction getDirection() {
    return direction;
  }

  public String getContent() {
    return content;
  }

  public String toString() {
    return direction + ": " + content;
  }

} // HistoryEntry
