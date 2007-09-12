package tigase.test.junit;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import tigase.test.util.XMLIO;
import tigase.xml.Element;

public abstract class JUnitXMLIO implements XMLIO {

	private Queue<Element> outQueue = new LinkedList<Element>();

	@Override
	public Queue<Element> read() throws IOException {
		return this.outQueue;
	}

	protected void send(Collection<Element> elements) {
		this.outQueue.addAll(elements);
	}

	protected void send(Element element) {
		this.outQueue.add(element);
	}

	public abstract void write(Element data) throws IOException;

	@Override
	public void write(String data) throws IOException {
		throw new RuntimeException("Please use write(Element data)");
	}

}
