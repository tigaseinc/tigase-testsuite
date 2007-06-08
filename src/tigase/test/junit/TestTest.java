package tigase.test.junit;

import java.io.IOException;

import org.junit.Test;

import tigase.xml.Element;

public class TestTest extends XMPPTestCase {

	public TestTest() {
		super("junit-test.cor");
	}

	@Test
	public void test_1() {
		final ExampleService service = new ExampleService();
		JUnitXMLIO xmlio = new JUnitXMLIO() {
			@Override
			public void write(Element data) throws IOException {
				send(service.doSomething(data));
			}
		};
		test(xmlio);
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
		test(xmlio);
	}

}
