package tigase.test.util;

import java.io.IOException;
import java.util.Queue;

import tigase.xml.Element;

public interface XMLIO {

	Queue<Element> read() throws IOException;

	void write(Element data) throws IOException;

	void write(String data) throws IOException;

}
