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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import tigase.test.util.HTMLFilter;
import tigase.test.util.OutputFilter;
import tigase.test.util.Params;
import tigase.test.util.ParserHandler;
import tigase.test.util.ScriptParser;
import tigase.xml.Element;

import static tigase.test.util.TestUtil.*;

/**
 * Describe class TestLoader here.
 *
 *
 * Created: Sat May 28 07:09:44 2005
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
@Deprecated
public class TestLoader implements ParserHandler {

  private static final String GLOB_SET_START = "GLOBAL SETTINGS:";
  private static final String TESTS_START = "TESTS:";
  private static final String DESCR_START = ">>";
  private static final String DESCR_END = "<<";
  private static final String TEST_SCRIPT_START = "{";
  private static final String TEST_SCRIPT_END = "}";

  private Params params = null;
  private String scriptName = "xmpp-tests.xmpt";
  private String title = null;
  private TestOld globalSettings = null;
  private List<TestOld> tests = null;
  private TestOld temp_test = null;
  private boolean stop_on_fail = false;

  /**
   * Creates a new <code>TestsLoader</code> instance.
   *
   */
  public TestLoader(Params params) {
    this.params = params;
    tests = new LinkedList<TestOld>();
  }

  public void loadTests() throws IOException {
    scriptName = params.get("-script", scriptName);
    debug("Script name: " + scriptName + "\n", true);
    ScriptParser parser = new ScriptParser(scriptName, this);
    parser.parseScript();
  }

  public int runTests() throws IOException {
    Params gtp = new Params();
    // Add global test parameters.
    gtp.putAll(globalSettings.getParams());
    debug("\n" + globalSettings.getDescription() + "\n\n", true);
    //    debug(globalSettings.getParams().toString() + "\n", true);
    // Add command line parameters - command line parameters
    // overwrite global params from script file but does not
    // overwrite test local params.
    gtp.putAll(params);
    stop_on_fail = gtp.containsKey("-stop-on-fail");
    //    debug("stop_on_fail: " + stop_on_fail + "\n", true);
    //    debug(gtp.toString() + "\n", true);
    OutputFilter filter = null;
    File dir_name = new File("");
    if (gtp.get("-output-format") != null
      && gtp.get("-output-format", "html").equals("html")) {
      File file_name =
        new File(gtp.get("-output-file", "functional-tests.html"));
      dir_name = file_name.getAbsoluteFile().getParentFile();
      BufferedWriter bw = new BufferedWriter(new FileWriter(file_name, false));
      filter = new HTMLFilter();
      filter.init(bw, title, globalSettings.getDescription());
      String ver_params_str = "-test-ns iq-version -loop 1 -output-history yes";
      TestOld tmp_test = new TestOld("Version");
      tmp_test.setParams(new Params(ver_params_str));
      tmp_test.runTest(gtp);
      if (tmp_test.getResult()) {
        Element elem = tmp_test.getLastResult();
        filter.addContent("<p>Server version info:<br/>\n"
          + "Name: " + elem.getCData("/iq/query/name") + "<br/>\n"
          + "Version: " + elem.getCData("/iq/query/version") + "<br/>\n"
          + "OS: " + elem.getCData("/iq/query/os") + "</p>\n");
      }
      String stats_params_str = "-test-ns iq-stats -loop 1 -output-history yes";
			stats_params_str += " -user-name " + gtp.get("-user-name");
			stats_params_str += " -user-pass " + gtp.get("-user-pass");
			stats_params_str += " -user_resr " + gtp.get("-user_resr");
			stats_params_str += " -def-auth " + gtp.get("-def-auth");
      tmp_test = new TestOld("Stats");
      tmp_test.setParams(new Params(stats_params_str));
      tmp_test.runTest(gtp);
      if (tmp_test.getResult()) {
        Element elem = tmp_test.getLastResult();
        filter.addContent("<p>Server stats before test:");
        filter.addContent("<table>");
        List<Element> items = elem.getChildren("/iq/query");
        for (Element item : items) {
          if (item.getName().equals("item")) {
            filter.addContent("<tr>"
              + "<td>" + item.getAttribute("name") + ":</td>"
              + "<td>" + item.getAttribute("value") + "</td>"
              + "<td>" + item.getAttribute("unit") + "</td>"
              + "<td>" + item.getCData() + "</td>" + "</tr>");
          } // end of if (item.getName().equals("item"))
        } // end of for (Element item : items)
        filter.addContent("</table>");
        filter.addContent("</p>\n");
      }
      if (gtp.get("-loop", 1) > 1) {
        filter.setColumnHeaders("Test name", "Result", "Total time",
          "OK", "Average", "Description");
      } // end of if (gtp.get("-loop", 1) > 1)
      else {
        filter.setColumnHeaders("Test name", "Result", "Test time",
          "Description", "History");
      } // end of if (gtp.get("-loop", 1) > 1) else
    }
    for (TestOld test : tests) {
      // This is not real test, so skip processing it here
      String test_result = null;
      if (test.getName().equalsIgnoreCase("version")
          || test.getName().equalsIgnoreCase("title")) {
        continue;
      } // end of if (test.getName().equalsIgnoreCase("version"))
      debug(test.getName() + ": ... ", true);
      test.runTest(gtp);
      if (test.getParams().containsKey("-no-record")) {
        debug(test.getDescription() + "\n", true);
        continue;
      } // end of if (gtp.containsKey("-no-record"))
      if (test.getResult()) {
        debug("success,  ", true);
        test_result = "success";
      } else {
        debug("FAILURE,  (" + test.getErrorMsg() + "),  ", true);
        test_result = "FAILURE";
      } // end of if (test.getResult()) else
      if (test.getTestsOK() > 0) {
        int loop = gtp.get("-loop", 1);
        if (test.getParams().containsKey("-loop")) {
          loop = test.getParams().get("-loop", 1);
        } // end of if (test.getParams().containsKey("-loop"))
        if (loop > 1) {
          debug("Total: " + test.getTestsTotalTime()
            + "ms, Average: "
            + (test.getSuccessfulTotalTime() / test.getTestsOK())
            + "ms, Loop: " + loop
            + ", OK: " + test.getTestsOK()
            + ", " + test.getDescription() + "\n", true);
        } else {
          debug("Total: " + test.getTestsTotalTime()
            + "ms,  " + test.getDescription() + "\n", true);
        } // end of else
      } else {
        debug("Total: " + test.getTestsTotalTime()
          + "ms,  " + test.getDescription() + "\n", true);
      } // end of else
      if (stop_on_fail && !test.getResult()) {
        break;
      } // end of if (stop_on_fail)
      if (filter != null) {
        if (gtp.get("-loop", 1) > 1) {
          long average = test.getTestsOK() > 0 ?
            (test.getSuccessfulTotalTime() / test.getTestsOK()) : 0;
          filter.addRow(test.getName(), test_result,
            "" + (test.getTestsTotalTime() / 1000) + " sec",
            "" + test.getTestsOK(),
            "" + average + " ms",
            test.getDescription());
        } // end of if (gtp.get("-loop", 1) > 1)
        else {
          String hist_file = test.getName().replace(' ', '_') + ".xml";
          filter.addRow(test.getName(), test_result,
            "" + test.getTestsTotalTime() + " ms", test.getDescription(),
            "<a href='" + hist_file + "'>" + test.getName() + "</a>");
          saveHistory(test, new File(dir_name, hist_file));
        } // end of if (gtp.get("-loop", 1) > 1) else
      } // end of if (filter != null)
    } // end of for ()
    if (filter != null) {
      String stats_params_str = "-test-ns iq-stats -loop 1 -output-history yes";
      TestOld tmp_test = new TestOld("Stats");
      tmp_test.setParams(new Params(stats_params_str));
      tmp_test.runTest(gtp);
      StringBuilder closingInfo = new StringBuilder();
      if (tmp_test.getResult()) {
        Element elem = tmp_test.getLastResult();
        closingInfo.append("<p>Server stats after test:");
        closingInfo.append("<table>");
        List<Element> items = elem.getChildren("/iq/query");
        for (Element item : items) {
          if (item.getName().equals("item")) {
            closingInfo.append("<tr>"
              + "<td>" + item.getAttribute("name") + ":</td>"
              + "<td>" + item.getAttribute("value") + "</td>"
              + "<td>" + item.getAttribute("unit") + "</td>"
              + "<td>" + item.getCData() + "</td>" + "</tr>");
          } // end of if (item.getName().equals("item"))
        } // end of for (Element item : items)
        closingInfo.append("</table>");
        closingInfo.append("</p>\n");
      }
      filter.close(closingInfo.toString());
    } // end of if (filter != null)
    return tests.size();
  }

  private void saveHistory(TestOld test, File file_name) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(file_name, false));
    for (HistoryEntry entry : test.getHistory()) {
      bw.write(entry.toString() + "\n");
    } // end of for ()
    bw.close();
  }

  public void handleTestName(String name) {
    debug("handleTestName: " + name + "\n", false);
    if (temp_test != null) {
      if (temp_test.getName().equalsIgnoreCase("global settings")) {
        globalSettings = temp_test;
      } else {
        tests.add(temp_test);
      } // end of else
      if (temp_test.getName().equalsIgnoreCase("title")) {
        title = temp_test.getDescription();
      }
    } // end of if (temp_test != null)
    temp_test = new TestOld(name);
  }

  public void handleTestParams(Params params) {
    debug("handleTestParams\n", false);
    //    debug(params.toString() + "\n", true);
    if (temp_test != null) {
      temp_test.setParams(params);
    } // end of if (temp_test == null)
  }

  public void handleTestDescription(String descr) {
    debug("handleTestDescription: " + descr + "\n", false);
    if (temp_test != null) {
      temp_test.setDescription(descr);
    } // end of if (temp_test == null)
  }

  public void handleScriptEnd() {
    if (temp_test != null) {
      if (temp_test.getName().equalsIgnoreCase("global settings")) {
        globalSettings = temp_test;
      } else {
        tests.add(temp_test);
      } // end of else
    } // end of if (temp_test != null)
  }

} // TestLoader
