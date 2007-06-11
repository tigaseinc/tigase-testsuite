package tigase.test.junit;

import tigase.xml.Element;

public class ScriptEntry {

	private ScriptEntryKind kind = null;
	private Element[] stanza = null;

	public ScriptEntry(ScriptEntryKind kind, Element[] stanza) {
		this.kind = kind;
		this.stanza = stanza;
	}

	/**
	 * @return the kind
	 */
	public ScriptEntryKind getKind() {
		return kind;
	}

	/**
	 * @return the stanza
	 */
	public Element[] getStanza() {
		return stanza;
	}

}