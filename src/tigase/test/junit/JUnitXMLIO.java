package tigase.test.junit;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import tigase.test.util.XMLIO;
import tigase.xml.Element;

public class JUnitXMLIO implements XMLIO {

	@Override
	public Queue<Element> read() throws IOException {
		System.out.println("read");
		final Element iq = new Element("iq", new String[] { "id", "type" }, new String[] { "1", "result" });
		iq.addChild(new Element("vCard", new String[]{"xmlns"}, new String[]{"vcard-temp"}));
		return new LinkedList<Element>() {
			{
				add(iq);
			}
		};
	}

	@Override
	public void write(Element data) throws IOException {
		System.out.println(data);

	}

	@Override
	public void write(String data) throws IOException {
		System.out.println(data);
	}

}
