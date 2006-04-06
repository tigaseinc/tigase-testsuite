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
 * Describe class ResultCode here.
 *
 *
 * Created: Sun May 22 23:28:21 2005
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public enum ResultCode {

  TEST_OK("No errors during test."),
  SOCKET_NOT_INITALIZED("Network socket is not ready."),
  NO_TESTS_DEFINED("There is no tests defined."),
  PROCESSING_EXCEPTION("Exception during processing: "),
  RESULT_DOESNT_MATCH("Received result doesn't match expected result.")
  ;

  String resultMsg = null;

  /**
   * Creates a new <code>ResultCode</code> instance.
   *
   */
  private ResultCode(String resultMsg) { this.resultMsg = resultMsg; }

  public String getMessage() { return resultMsg; }

} // ResultCode
