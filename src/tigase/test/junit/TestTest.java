package tigase.test.junit;

import java.io.IOException;

import org.junit.Test;

import tigase.xml.Element;

public class TestTest extends XMPPTestCase {

	@Test
	public void test_1() {
		final ExampleService service = new ExampleService();
		JUnitXMLIO xmlio = new JUnitXMLIO() {
			@Override
			public void write(Element data) throws IOException {
				send(service.doSomething(data));
			}
		};
		test("junit-test.cor", xmlio);
	}

	@Test
	public void test_2() {
		final ExampleService service = new ExampleService();
		JUnitXMLIO xmlio = new JUnitXMLIO() {
			@Override
			public void write(Element data) throws IOException {
				send(service.doSomethingElse(data));
			}
		};
		test("junit-test-other.cor", xmlio);
	}

}
