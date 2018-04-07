package org.owasp.html;

public class CharReplacements {

	public static final CharReplacements DEFAULT = new CharReplacements();

	public static final String PLAINTEXT_BRACE_REPLACEMENT = "{<!-- -->";
	public static final String TAG_BRACE_REPLACEMENT       = "{\u200B";
	
	private String plaintextBraceReplacement = PLAINTEXT_BRACE_REPLACEMENT;
	private String tagBraceReplacement = TAG_BRACE_REPLACEMENT;
	
	/**
	 * Maps ASCII chars that need to be encoded to an equivalent HTML entity.
	 */
	String[] replacementTable;

	public CharReplacements(){
		this.replacementTable = initDefaultReplacementsTable();
	}
	
	public CharReplacements(CharReplacements oth){
		this.replacementTable = new String[oth.replacementTable.length];
		System.arraycopy(oth.replacementTable, 0, this.replacementTable, 0, oth.replacementTable.length);
	}
	
	public CharReplacements clone(){
		return new CharReplacements(this);
	}
	
	public CharReplacements setPlaintextBraceReplacement(String braceReplacement) {
		this.plaintextBraceReplacement = braceReplacement;
		return this;
	}
	
	public CharReplacements setTagBraceReplacement(String braceReplacement) {
		this.tagBraceReplacement = braceReplacement;
		return this;
	}

	public CharReplacements dontReplace(char ... chars){
		for (char ch: chars)
			if (ch < replacementTable.length)
				replacementTable[ch] = null;
		
		return this;
	}
	
	public boolean hasReplacementForChar(char ch) {
		return ch < replacementTable.length;
	}

	public String getReplacementForChar(char ch, String text, int pos, int textLen, boolean isInPlaintextMode) {
		String repl = replacementTable[ch];

		if (repl != null)
			return repl;

		if (isDoubleBrace(ch, text, pos, textLen))
			repl = isInPlaintextMode ? plaintextBraceReplacement : tagBraceReplacement;

		return repl;
	}

	private boolean isDoubleBrace(char ch, String text, int pos, int textLen) {
		return ch == '{' && 
			  (pos + 1 == textLen || text.charAt(pos + 1) == '{');
	}

	private String[] initDefaultReplacementsTable() {
		String[] replTbl = new String[0x80];
		
		for (int i = 0; i < ' '; ++i) {
			// We elide control characters so that we can ensure that our output
			// is
			// in the intersection of valid HTML5 and XML. According to
			// http://www.w3.org/TR/2008/REC-xml-20081126/#charsets
			// Char ::= #x9 | #xA | #xD | [#x20-#xD7FF]
			// | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
			if (i != '\t' && i != '\n' && i != '\r') {
				replTbl[i] = ""; // Elide
			}
		}
		// "&#34;" is shorter than "&quot;"
		replTbl['"'] = "&#" + ((int) '"') + ";"; // Attribute delimiter.
		replTbl['&'] = "&amp;"; // HTML special.
		// We don't use &apos; since that is not in the intersection of
		// HTML&XML.
		replTbl['\''] = "&#" + ((int) '\'') + ";"; // Attribute delimiter.
		replTbl['+'] = "&#" + ((int) '+') + ";"; // UTF-7 special.
		replTbl['<'] = "&lt;"; // HTML special.
		replTbl['='] = "&#" + ((int) '=') + ";"; // Special in attributes.
		replTbl['>'] = "&gt;"; // HTML special.
		replTbl['@'] = "&#" + ((int) '@') + ";"; // Conditional
														// compilation.
		replTbl['`'] = "&#" + ((int) '`') + ";"; // Attribute delimiter.
		
		return replTbl;
	}
}
