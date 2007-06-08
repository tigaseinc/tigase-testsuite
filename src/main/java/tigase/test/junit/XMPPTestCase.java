package tigase.test.junit;

import static org.junit.Assert.fail;
import tigase.test.ResultCode;
import tigase.test.impl.TestCommon;
import tigase.test.util.Params;

public abstract class XMPPTestCase {

	/**
	 * Method run test.
	 * 
	 * @param xmlio
	 */
	public static void test(final String configFile, final JUnitXMLIO xmlio) {
		if (configFile == null) {
			throw new RuntimeException("Script file must be specified.");
		}
		tigase.test.util.Params par = new Params();
		par.put("-source-file", configFile);
		par.put("socketxmlio", xmlio);

		TestCommon t = new TestCommon();
		t.init(par);

		t.run();

		if (t.getResultCode() != ResultCode.TEST_OK) {
			fail(t.getResultMessage());
		}

	}

	private String configFile;

	public XMPPTestCase() {
		this.configFile = null;
	}

	/**
	 * 
	 * @param sourceFile
	 *            file with test script
	 */
	public XMPPTestCase(String sourceFile) {
		this.configFile = sourceFile;
	}

	public void test(final JUnitXMLIO xmlio) {
		test(this.configFile, xmlio);
	}

}
