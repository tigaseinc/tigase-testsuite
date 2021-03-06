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
package tigase.test;

/**
 * Describe class ResultCode here.
 *
 *
 * Created: Sun May 22 23:28:21 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public enum ResultCode {

  TEST_OK("No errors during test.")
		, SOCKET_NOT_INITALIZED("Network socket is not ready.")
		, NO_TESTS_DEFINED("There is no tests defined.")
		, PROCESSING_EXCEPTION("Exception during processing: ")
		,	RESULT_DOESNT_MATCH("Received result doesn't match expected result.")
		, UNEXPECTED_INPUT("Unpexpected input from the socket.")
  ;

  String resultMsg = null;

  /**
   * Creates a new <code>ResultCode</code> instance.
   *
   */
  private ResultCode(String resultMsg) { this.resultMsg = resultMsg; }

  public String getMessage() { return resultMsg; }

} // ResultCode