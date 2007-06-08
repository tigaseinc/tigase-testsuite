package tigase.test.junit;

import java.util.ArrayList;
import java.util.List;

import tigase.xml.Element;

public class ExampleService {

	public List<Element> doSomething(Element element) {
		List<Element> result = new ArrayList<Element>();

		result.add(element.clone());

		return result;
	}

	public List<Element> doSomethingElse(Element element) {
		List<Element> result = new ArrayList<Element>();

		result.add(element.clone());

		return result;
	}
	
}
