package tigase.test.junit;

import static org.junit.Assert.fail;
import tigase.test.ResultCode;
import tigase.test.impl.TestCommon;
import tigase.test.util.Params;

public abstract class XMPPTestCase {

	private String configFile;

	/**
	 * 
	 * @param sourceFile file with test script
	 */
	public XMPPTestCase(String sourceFile) {
		this.configFile = sourceFile;
	}

	/**
	 * Method run test.
	 * @param xmlio
	 */
	public void test(final JUnitXMLIO xmlio) {
		tigase.test.util.Params par = new Params();
		par.put("-source-file", this.configFile);
		par.put("socketxmlio", xmlio);

		TestCommon t = new TestCommon();
		t.init(par);

		t.run();

		if (t.getResultCode() != ResultCode.TEST_OK) {
			fail(t.getResultMessage());
		}

	}

}
