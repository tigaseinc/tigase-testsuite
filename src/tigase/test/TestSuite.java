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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.net.Socket;
import tigase.test.util.Params;

import static tigase.test.util.TestUtil.*;

/**
 * Describe class TestSuite here.
 *
 *
 * Created: Tue May 17 19:14:30 2005
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestSuite {

  /**
   * Creates a new <code>TestSuite</code> instance.
   *
   */
  private TestSuite() { }

  private static String help() {
    return "\n"
      + "Parameters:\n"
      + " -key value      This test accepts parameters in this form\n"
      + "                 refer to particular test implementation for information\n"
      + "                 about accepted parameters\n"
      ;
  }

  /**
   * Describe <code>main</code> method here.
   *
   * @param args a <code>String[]</code> value
   */
  public static void main(final String[] args) throws Exception {

    Params params = new Params(args);
    TestScriptLoader tl = new TestScriptLoader(params);
    tl.loadTests();
    tl.runTests();

  }

} // TestSuite
