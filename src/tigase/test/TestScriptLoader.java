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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import tigase.test.parser.TestNode;
import tigase.test.parser.TestScript;
import tigase.test.util.HTMLFilter;
import tigase.test.util.NullFilter;
import tigase.test.util.OutputFilter;
import tigase.test.util.Params;
import tigase.test.util.TestUtil;

import static tigase.test.util.TestUtil.*;

/**
 * Describe class TestScriptLoader here.
 *
 *
 * Created: Sat Jul  9 00:38:27 2005
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestScriptLoader {

  private TestScript parser = null;
  private Params params = null;
  private List<TestNode> testNodes = null;
  private String scriptName = "xmpp-tests.xmpt";
  private File dir_name = new File("");
  private OutputFilter filter = null;
  private int output_cols = 5;
  private boolean stop_on_fail = false;
  private boolean output_history = false;
  private JobControll jobContr = new JobControll();
  private Test multiResCont = null;

  /**
   * Creates a new <code>TestScriptLoader</code> instance.
   *
   */
  public TestScriptLoader(Params params) {
    this.params = params;
  }

	@SuppressWarnings({"unchecked"})
  public void loadTests() throws Exception {
    scriptName = params.get("-script", scriptName);
    debug("Script name: " + scriptName + "\n", true);
    parser =
      new TestScript(new InputStreamReader(new FileInputStream(scriptName),
          "UTF-8"));
    parser.Input();
    testNodes = parser.getTests();
    Params pars = new Params(parser.getGlobalPars());
    pars.putAll(params);
    params = pars;
    stop_on_fail = params.get("-stop-on-fail", false);
  }

	@SuppressWarnings({"unchecked"})
  public void runTests() throws Exception {
    filter = initOutputFilter();
    for (TestNode node: testNodes) {
      node.addGlobalPars(parser.getGlobalPars());
      node.addGlobalVars(parser.getGlobalVars());
      boolean result = runTest(node);
      if (stop_on_fail && !result) {
        break;
      } // end of if (stop_on_fail)
    } // end of for (TestNode node : testNodes)
    //    jobContr.allDone();
    closeFilter(filter);
  }

  private boolean runTest(TestNode node) throws Exception {
    if (node.getPars().containsKey("-multi-thread")) {
      debug(node.getName() + ": " + getDescription(node) + " ... ", true);
      multiResCont = new Test(node);
      multiResCont.getParams().put("-loop", 0);
      runMultiTest(node);
      jobContr.allDone(0);
      calculateResult(multiResCont);
      return true;
    } // end of if (test.getParams().containsKey("-multithread"))
    boolean result = true;
    if (node.getId() != null) {
      debug(node.getName() + ": " + getDescription(node) + " ... ", true);
      Test test = new Test(node);
      test.runTest();
      calculateResult(test);
      result = test.getResult();
    }
    if (stop_on_fail && !result) {
      return result;
    } // end of if (stop_on_fail)
    if (node.getChildren() != null) {
      for (TestNode child: node.getChildren()) {
        result = runTest(child);
        if (stop_on_fail && !result) {
          return result;
        } // end of if (stop_on_fail)
				if (node.getPars().containsKey("-delay")) {
					long delay = Long.decode(node.getPars().get("-delay").toString());
					if (delay > 0) {
						try { Thread.sleep(delay);
						} catch (InterruptedException e) { } // end of try-catch
					} // end of if (delay > 0)
				} // end of if (main_params.containsKey("-delay"))
      } // end of for (TestNode child: node.getChildren())
    } // end of if (node.getChildren() != null)
    return result;
  }

  private boolean runMultiTest(TestNode node) throws IOException {
    if (node.getId() != null) {
      Test test = new Test(node);
      ThreadTest tt = new ThreadTest(test);
      new Thread(tt).start();
    }
    if (node.getChildren() != null) {
      for (TestNode child: node.getChildren()) {
        runMultiTest(child);
      } // end of for (TestNode child: node.getChildren())
    } // end of if (node.getChildren() != null)
    return true;
  }

  private synchronized void calculateMultiResult(Test test) {
    Params multiParams = multiResCont.getParams();
    multiParams.put("-loop",
      multiParams.get("-loop", 0) + test.getParams().get("-loop", 1));
    multiResCont.addTestsTotalTime(test.getTestsTotalTime());
    multiResCont.addSuccessfulTotalTime(test.getSuccessfulTotalTime());
    multiResCont.addTestsOK(test.getTestsOK());
    multiResCont.addTestsErr(test.getTestsErr());
    if (output_history) {
      multiResCont.getHistory().addAll(test.getHistory());
    } // end of if (output_history)
    multiResCont.setResult(
      (multiResCont.getTestsOK() > multiResCont.getTestsErr()));
  }

  private void calculateResult(Test test) throws IOException {
    debugResult(test);
    if (!test.getParams().containsKey("-no-record")) {
      filterResult(test);
    } // end of if (!test.getParams().containsKey("-no-record"))
  }

  private void debugResult(Test test) {
    if (test.getResult()) {
      debug("success,  ", true);
    } else {
      debug("FAILURE,  (" + test.getErrorMsg() + "),  ", true);
    } // end of if (test.getResult()) else
    if (test.getTestsOK() > 0) {
      int loop = test.getParams().get("-loop", 1);
      if (loop > 1) {
        debug("Total: " + test.getTestsTotalTime()
          + "ms, Average: "
          + (test.getSuccessfulTotalTime() / test.getTestsOK())
          + "ms, Loop: " + loop
          + ", OK: " + test.getTestsOK()
          + "\n", true);
      } else {
        debug("Total: " + test.getTestsTotalTime()
          + "ms\n", true);
      } // end of else
    } else {
      debug("Total: " + test.getTestsTotalTime()
        + "ms\n", true);
    } // end of else
  }

  private void filterResult(Test test) throws IOException {
    String test_result = null;
    if (test.getResult()) {
      test_result = "success";
    } else {
      test_result = "FAILURE";
    } // end of if (test.getResult()) else
    String hist_file = test.getName().replace(' ', '_') + ".xml";
    if (output_history) {
      saveHistory(test, new File(dir_name, hist_file));
    } // end of if (!params.get("-output-history", true))
    long average = test.getTestsOK() > 0 ?
      (test.getSuccessfulTotalTime() / test.getTestsOK()) : 0;
    switch (output_cols) {
    case 7:
      filter.addRow(test.getName(), test_result,
        "" + (test.getTestsTotalTime() / 1000) + " sec",
        "" + test.getTestsOK(),
        "" + average + " ms",
        test.getDescription(),
        "<a href='" + hist_file + "'>" + test.getName() + "</a>");
      break;
    case 6:
      filter.addRow(test.getName(), test_result,
        "" + (test.getTestsTotalTime() / 1000) + " sec",
        "" + test.getTestsOK(),
        "" + average + " ms",
        test.getDescription());
      break;
    case 5:
      filter.addRow(test.getName(), test_result,
        "" + test.getTestsTotalTime() + " ms", test.getDescription(),
        "<a href='" + hist_file + "'>" + test.getName() + "</a>");
      break;
    case 4:
    default:
      filter.addRow(test.getName(), test_result,
        "" + test.getTestsTotalTime() + " ms", test.getDescription());
      break;
    } // end of switch (output_cols)
  }

  private OutputFilter initOutputFilter() throws IOException {
    OutputFilter filter = null;
    if (params.get("-output-format") != null
      && params.get("-output-format", "html").equals("html")) {
      File file_name =
        new File(params.get("-output-file", "functional-tests.html"));
      dir_name = file_name.getAbsoluteFile().getParentFile();
      BufferedWriter bw = new BufferedWriter(new FileWriter(file_name, false));
      filter = new HTMLFilter();
      filter.init(bw, params.get("-title", ""), getDescription());
      Map<String, String> ver_map = getVersion();
      if (ver_map != null && ver_map.size() > 0) {
        filter.addContent("   <p>Server version info:<br/>\n");
        for (Map.Entry entry: ver_map.entrySet()) {
          filter.addContent("     " + entry.getKey() + ": " + entry.getValue()
            + "<br/>\n");
        } // end of for ()
      }
      List<StatItem> stat_list = getStatistics();
      filter.addContent(outputStatistics(stat_list, "before"));
      output_cols = params.get("-output-cols", 5);
      output_history = params.get("-output-history", true);
      if (!output_history) {
        --output_cols;
      } // end of if (!params.get("-output-history", true))
      switch (output_cols) {
      case 7:
        filter.setColumnHeaders("Test name", "Result", "Total time",
          "OK", "Average", "Description", "History");
        break;
      case 6:
        filter.setColumnHeaders("Test name", "Result", "Total time",
          "OK", "Average", "Description");
        break;
      case 5:
        filter.setColumnHeaders("Test name", "Result", "Test time",
          "Description", "History");
        break;
      case 4:
      default:
        filter.setColumnHeaders("Test name", "Result", "Test time",
          "Description");
        break;
      } // end of switch (output_cols)
    } else {
      filter = new NullFilter();
    } // end of else
    return filter;
  }

  private void closeFilter(OutputFilter filter) throws IOException {
    filter.close(outputStatistics(getStatistics(), "after"));
  }

	@SuppressWarnings({"unchecked"})
  private Map<String, String> getVersion() {
    TestNode node = getTestNode("Version");
    if (node != null) {
      Test tmp_test = new Test(node);
      tmp_test.runTest();
      return (Map<String, String>) tmp_test.getParams().get("Version");
    } // end of if (node != null)
    return null;
  }

  private String outputStatistics(List<StatItem> stats, String when) {
    StringBuilder sb = new StringBuilder();
    if (stats != null && stats.size() > 0) {
      sb.append("  <p>Server stats " + when + " test:\n");
      sb.append("   <table>\n");
      for (StatItem item : stats) {
        sb.append("    <tr>"
          + "<td>" + item.getName() + ":</td>"
          + "<td>" + item.getValue() + "</td>"
          + "<td>" + item.getUnit() + "</td>"
          + "<td>" + item.getDescription() + "</td>" + "</tr>\n");
      } // end of for ()
      sb.append("   </table>\n");
      sb.append("  </p>\n");
    }
    return sb.toString();
  }

	@SuppressWarnings({"unchecked"})
  private List<StatItem> getStatistics() {
    TestNode node = getTestNode("Statistics");
    if (node != null) {
			node.addGlobalPars(parser.getGlobalPars());
			node.addGlobalVars(parser.getGlobalVars());
      Test tmp_test = new Test(node);
      tmp_test.runTest();
      return (List<StatItem>) tmp_test.getParams().get("Statistics");
    } // end of if (node != null)
    return null;
  }

  private TestNode getTestNode(String testName) {
    for (TestNode node : testNodes) {
      if (testName.equalsIgnoreCase(node.getName())) {
        return node;
      } // end of if (testName.equalsIgnoreCase(node.getName()))
    } // end of for ()
    return null;
  }

  private String getDescription() {
    StringBuilder sb = new StringBuilder();
    sb.append("<b>\n");
    sb.append("   <ol>\n");
    for (TestNode node : testNodes) {
      String descr = node.getLongDescr();
      sb.append("    <li>" + descr.substring(2, descr.length() - 2)
        + "</li>\n");
    } // end of for (TestNode node : testNodes)
    sb.append("   </ol>\n");
    sb.append("  </b>\n");
    return sb.toString();
  }

  private void saveHistory(Test test, File file_name) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(file_name, false));
    for (HistoryEntry entry: test.getHistory()) {
      bw.write(entry.toString() + "\n");
    } // end of for ()
    bw.close();
  }

  private String getDescription(TestNode node) {
    String descr = node.getShortDescr();
    if (descr == null || descr.trim().equals("")) {
      String ldescr = node.getLongDescr();
      descr = ldescr.substring(2, ldescr.length() - 2);
    } // end of if (descr == null || descr.trim().equals(""))
    return descr;
  }

  private class JobControll {
    private int running = 0;
    private Lock lock = new ReentrantLock();
    private Condition done = lock.newCondition();

    public boolean allDone(long sleep) throws InterruptedException {
      lock.lock();
      try {
        if (sleep > 0) Thread.sleep(sleep);
        while (running > 0) done.await();
        return true;
      } finally {
        lock.unlock();
      } // end of try-finally
    }

    public void threadStarted() {
      lock.lock();
      try {
        ++running;
      } finally {
        lock.unlock();
      } // end of try-finally
    }

    public void threadDone() {
      lock.lock();
      try {
        --running;
        if (running <= 0) {
          done.signal();
        } // end of if (running <= 0)
      } finally {
        lock.unlock();
      } // end of try-finally
    }

  }

  private class ThreadTest implements Runnable {
    private Test test = null;

    public ThreadTest(Test test) {
      jobContr.threadStarted();
      this.test = test;
    }

    // Implementation of java.lang.Runnable

    /**
     * Describe <code>run</code> method here.
     *
     */
    public void run() {
      test.runTest();
      try {
        calculateMultiResult(test);
      } // end of try
      catch (Exception e) {
        e.printStackTrace();
      } // end of try-catch
      jobContr.threadDone();
    }

  }

} // TestScriptLoader
