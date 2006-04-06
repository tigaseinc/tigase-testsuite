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

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import tigase.test.parser.TestNode;
import tigase.test.util.Params;
import tigase.xml.Element;

import static tigase.test.util.TestUtil.*;

/**
 * Describe class Test here.
 *
 *
 * Created: Sat May 28 08:05:39 2005
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestOld {

  public static final String NS_SEP = ";";

  private String testName = null;
  private Params params = null;
  private String description = null;
  private boolean result = false;
  private String errorMsg = null;
  private Exception exception = null;
  private List<HistoryEntry> history = null;
  private int tests_ok = 0;
  private int tests_er = 0;
  private long total_time = 0;
  private long total_successful = 0;
  private boolean collectHistory = false;
  private boolean on_one_socket = false;
  private boolean active_connection = false;
  private Element lastResult = null;

  /**
   * Creates a new <code>Test</code> instance.
   *
   */
  public TestOld(String name, Params params, String descr) {
    testName = name;
    this.params = params;
    description = descr;
  }

  public TestOld(String name) {
    testName = name;
  }

  public void runTest(Params global) {
    Params main_params = new Params();
    main_params.putAll(global);
    main_params.putAll(params);
    boolean deb = main_params.containsKey("-debug");
    int loop = main_params.get("-loop", 1);
    String tmp_ns = main_params.get("-test-ns", "socket");
    String[] test_ns = tmp_ns.split(NS_SEP);
    String user_name = (String)main_params.get("-user-name");
    boolean loop_user_name = false;
    if (user_name != null && user_name.contains("$(loop)")) {
      loop_user_name = true;
      user_name = user_name.replace("$(loop)", "");
    } // end of if (user_name != null && user_name.contains("$(loop)"))
    boolean daemon = main_params.containsKey("-daemon");
    on_one_socket = main_params.containsKey("-on-one-socket");
    active_connection = main_params.containsKey("-active-connection");
    long loop_delay = 0;
    if (main_params.containsKey("-loop-delay")) {
      loop_delay = main_params.get("-loop-delay", 10);
    } // end of if (main_params.containsKey("-delay"))
    if (!main_params.isFalse("-output-history")
      && !params.containsKey("-daemon")) {
      collectHistory = true;
      history = new LinkedList<HistoryEntry>();
    } // end of if (!main_params.isFalse())
    List<TestIfc> suite = null;
    Params test_params = null;
    boolean this_result = false;
    for (int cnt = 0; cnt < loop; cnt++) {
      try {
        if (on_one_socket && cnt > 0 && this_result) {
          List<TestIfc> suite_tmp = getDependsTree(test_ns, test_params);
          suite = suite_tmp.subList(suite_tmp.size() - 1, suite_tmp.size());
        } else {
          test_params = new Params();
          test_params.putAll(main_params);
          suite = getDependsTree(test_ns, test_params);
        } // end of else
        if (suite.size() == 0) {
          errorMsg =
            "No tests implementation found for given name space: " + tmp_ns;
          result = false;
          return;
        } // end of if (suite.size() == 0)
        if (loop_user_name) {
          test_params.put("-user-name", user_name+cnt);
        } // end of if (loop_user_name)
        DaemonTest dt = new DaemonTest(suite, test_params, deb);
        long test_start_time = System.currentTimeMillis();
        if (daemon) {
          runThread(dt);
          synchronized(dt) { dt.wait(5000); }
          if (!dt.isAuthorized()) {
            runThread(dt);
            synchronized(dt) { dt.wait(5000); }
          } // end of if (!dt.isAuthorized())
          this_result = true;
        } // end of if (daemon)
        else {
          dt.run();
          this_result = dt.getResult();
        } // end of if (daemon) else
        long this_test = System.currentTimeMillis() - test_start_time;
        total_time += this_test;
        if (this_result) {
          total_successful += this_test;
          ++tests_ok;
        } // end of if (this_result)
        else {
          ++tests_er;
          if (cnt > 10 && (tests_ok <= tests_er)) {
            debug("Too many errors, stopping test...\n", deb);
            result = false;
            errorMsg =
              "Too many errors, stopping test: "
              + tests_ok + " OK, "
              + tests_er + " ER";
            return;
          } // end of if (cnt > 10 && (cnt / tests_err <= 2))
          String on_error = (String)test_params.get("-on-error");
          if (on_error != null) {
            int retries = test_params.get("-retries", 1);
            for (int error_cnt = 0; error_cnt < retries; error_cnt++) {
              String[] on_error_ns = on_error.split(NS_SEP);
              test_params = new Params();
              test_params.putAll(main_params);
              List<TestIfc> error_suite =
                getDependsTree(on_error_ns, test_params);
              if (suite.size() == 0) {
                errorMsg =
                  "No tests implementation found for given name space: "
                  + on_error;
                result = false;
                return;
              } // end of if (suite.size() == 0)
              DaemonTest dte = new DaemonTest(error_suite, test_params, deb);
              dte.run();
              if (dte.getResult()) {
                break;
              } // end of if ()
            } // end of for (int error_cnt = 0; error_cnt < retries; error_cnt++)
          } // end of if (on_error == null) else
        } // end of if (this_result) else
      } catch (Exception e) {
        result = false;
        errorMsg = e.getMessage();
        exception = e;
        return;
      } // end of try-catch
      if (loop_delay > 0) {
        try { Thread.sleep(loop_delay);
        } catch (InterruptedException e) { } // end of try-catch
      } // end of if (loop_delay > 0)
    } // end of for (int cnt = 0; cnt < loop; cnt++)
    result = tests_ok > tests_er;
    if (main_params.containsKey("-delay")) {
      long delay = main_params.get("-delay", 1000);
      try { Thread.sleep(delay);
      } catch (InterruptedException e) { } // end of try-catch
    } // end of if (main_params.containsKey("-delay"))
  }

  private void runThread(Runnable task) {
    Thread t = new Thread(task);
    t.setDaemon(true);
    t.start();
  }

  public int getTestsOK() {
    return tests_ok;
  }

  public int getTestsErr() {
    return tests_er;
  }

  public long getTestsTotalTime() {
    return total_time;
  }

  public long getSuccessfulTotalTime() {
    return total_successful;
  }

  public List<HistoryEntry> getHistory() {
    return history;
  }

  public Exception getException() {
    return exception;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public Element getLastResult() {
    return lastResult;
  }

  public boolean getResult() {
    return result;
  }

  public void setName(String name) {
    testName = name;
  }

  public String getName() {
    return testName;
  }

  public void setParams(Params params) {
    this.params = params;
  }

  public Params getParams() {
    return params;
  }

  public void setDescription(String descr) {
    description = descr;
  }

  public String getDescription() {
    return description;
  }

  class DaemonTest implements Runnable {

    private List<TestIfc> suite = null;
    private Params params = null;
    private boolean deb = false;
    private boolean result = false;
    private boolean authorized = false;

    public DaemonTest(List<TestIfc> suite, Params params, boolean deb) {
      this.suite = suite;
      this.params = params;
      this.deb = deb;
    }

    // Implementation of java.lang.Runnable

    /**
     * Describe <code>run</code> method here.
     *
     */
    public void run() {
      try {
        for (TestIfc test : suite) {
          debug("Testing: " + toStringArrayNS(test.implemented(), "..."), deb);
          test.init(params);
          boolean res = test.run();
          if (collectHistory) {
            history.addAll(test.getHistory());
          } // end of if (history)
          if (res) {
            lastResult = test.getLastResult();
            authorized = params.get("authorized", false);
            if (authorized) {
              synchronized(this) { this.notifyAll(); }
            } // end of if (authorized)
            debug("     success!\n", deb);
          } else {
            errorMsg = test.getResultMessage();
            debug("       failure!\n", deb);
            debug("Error code: " + test.getResultCode()
              + ", error message: " + test.getResultMessage() + "\n", deb);
            if (test.getHistory() != null) {
              for (HistoryEntry he : test.getHistory()) {
                debug("" + he.getDirection() + "\n"
                  + he.getContent() + "\n", deb);
              } // end of for ()
            } // end of if (test.getHistory() != null)
            result = false;
            synchronized(this) { this.notifyAll(); }
            return;
          } // end of if (test.run()) else
        } // end of for ()
      } // end of try
      catch (Exception e) {
        e.printStackTrace();
        exception = e;
        errorMsg = e.toString();
        result = false;
        synchronized(this) { this.notifyAll(); }
        return;
      } // end of try-catch
      if (!on_one_socket && !active_connection) {
        try { ((Socket)params.get("socket")).close();
        } catch (Exception e) { }
      }
      result = true;
      synchronized(this) { this.notifyAll(); }
    }

    public boolean getResult() {
      return result;
    }

    public boolean isAuthorized() {
      return authorized;
    }

  }

} // Test
