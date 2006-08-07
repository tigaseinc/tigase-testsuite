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

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Describe class HTMLFilter here.
 *
 *
 * Created: Thu Jun  9 20:10:31 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class HTMLFilter implements OutputFilter {

  private BufferedWriter bw  = null;
  private long start = 0;

  /**
   * Creates a new <code>HTMLFilter</code> instance.
   *
   */
  public HTMLFilter() { }

  // Implementation of tigase.test.util.OutputFilter

  /**
   * Describe <code>init</code> method here.
   *
   * @param out a <code>BufferedWriter</code> value
   * @param title a <code>String</code> value
   * @param description a <code>String</code> value
   */
  public void init(final BufferedWriter out, final String title,
    final String description) throws IOException {
    bw = out;
    bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
      + " \"http://www.w3.org/TR/html4/strict.dtd\">\n");
    bw.write("<html>\n");
    bw.write(" <head>\n");
    bw.write("  <title>" + title + "</title>\n");
    bw.write(" </head>\n");
    bw.write(" <body>\n");
    bw.write("  <h2>" + title + "</h2>\n");
    bw.write("  " + description + "\n");
    bw.write("  <p>Test start time: <b>" +
      DateFormat.getDateTimeInstance().format(new Date()) + "</b></p>\n");
    bw.flush();
    start = System.currentTimeMillis();
  }

  public void addContent(String content) throws IOException {
    bw.write(content);
    bw.flush();
  }

  /**
   * Describe <code>close</code> method here.
   *
   */
  public void close(String closingInfo) throws IOException {
    bw.write("  </table>\n");
    bw.write("  </p>\n");
    bw.write("  <p>Test end time: <b>" +
      DateFormat.getDateTimeInstance().format(new Date()) + "</b></p>\n");
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(System.currentTimeMillis() - start);
    //cal.computeFields();
    bw.write("  <p>Total test time:");
		bw.write(" " + (cal.get(Calendar.HOUR_OF_DAY) - 1) + " hours");
    //bw.write(" " + cal.get(Calendar.HOUR_OF_DAY) + " hours");
    bw.write(", " + cal.get(Calendar.MINUTE) + " minutes");
    bw.write(", " + cal.get(Calendar.SECOND) + " seconds");
    bw.write(", " + cal.get(Calendar.MILLISECOND) + " ms.</p>");
    bw.write(closingInfo);
    bw.write(" </body>\n");
    bw.write("</html>\n");
    bw.flush();
    bw.close();
  }

  /**
   * Describe <code>setColumnHeaders</code> method here.
   *
   * @param hd a <code>String[]</code> value
   */
  public void setColumnHeaders(final String ... hd) throws IOException {
    bw.write("  <p>\n");
    bw.write("  <table border='1'>\n");
    bw.write("   <thead valign='middle'>\n");
    bw.write("    <tr>\n");
    for (String header : hd) {
      bw.write("     <th>" + header + "</th>\n");
    } // end of for ()
    bw.write("    </tr>\n");
    bw.write("   </thead>\n");
    bw.flush();
  }

  /**
   * Describe <code>addRow</code> method here.
   *
   * @param cols a <code>String[]</code> value
   */
  public void addRow(final String ... cols) throws IOException {
    bw.write("   <tr>\n");
    for (String col : cols) {
      bw.write("    <td>" + col + "</td>\n");
    } // end of for ()
    bw.write("   </tr>\n");
    bw.flush();
  }

} // HTMLFilter
