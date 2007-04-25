/*  Package Jabber Server
 *  Copyright (C) 2001, 2002, 2003, 2004, 2005
 *  "Artur Hefczyc" <artur.hefczyc@tigase.org>
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
package tigase.test.util;

import tigase.xml.Element;
import javax.management.Attribute;
import java.util.List;
import java.util.Map;

/**
 * Describe class ElementUtil here.
 *
 *
 * Created: Wed May 18 16:45:12 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class ElementUtil {

  /**
   * Creates a new <code>ElementUtil</code> instance.
   *
   */
  public ElementUtil() {

  }

  public static boolean hasAttributes(Element elem, Attribute[] attrs) {
    for (Attribute attr : attrs) {
      String val = elem.getAttribute(attr.getName());
      if (val == null) {
        return false;
      } // end of if (val == null)
      else {
        if (!val.equals(attr.getValue())) {
          return false;
        } // end of if (!val.equals(attr.getValue()))
      } // end of if (val == null) else
    } // end of for ()
    return true;
  }

	public static boolean equalElems(Element el1, Element el2) {
		if (!el1.getName().equals(el2.getName())) {
			return false;
		} // end of if (!el1.getName().equals(el2.getName()))
		Map<String, String> attrs = el1.getAttributes();
		if (attrs != null) {
			for (String key: attrs.keySet()) {
				String atval2 = el2.getAttribute(key);
				if (atval2 == null) {
					return false;
				} // end of if (at2 == null)
				if (!attrs.get(key).equals(atval2)) {
					return false;
				} // end of if (!attrs.get(key).equals(atval2))
			} // end of for (String key: attrs.keySet())
		} // end of if (attrs != null)
		String cdata1 = el1.getCData();
		if (cdata1 != null) {
			String cdata2 = el2.getCData();
			if (cdata2 == null) {
				return false;
			} // end of if (cdata2 == null)
			if (!cdata1.equals(cdata2)) {
				return false;
			} // end of if (!cdata1.equals(cdata2))
		} // end of if (cdata1 != null)
		return true;
	}

	public static boolean equalElemsDeep(Element el1, Element el2) {
		boolean equal = equalElems(el1, el2);
		if (!equal) {
			return false;
		} // end of if (!res)
		List <Element> children = el1.getChildren();
		if (children == null) {
			return true;
		} // end of if (children == null)
		for (Element child1: children) {
			List<Element> children2 = el2.getChildren();
			if (children2 == null || children2.size() == 0) {
				return false;
			} // end of if (child2 == null)
			boolean found_child = false;
			for (Element child2: children2) {
				found_child |= equalElemsDeep(child1, child2);
			} // end of for (Element child2: children2)
			if (!found_child) {
				return false;
			} // end of if (!res)
		} // end of for (Element child: children)
		return true;
	}

} // ElementUtil
