package com.objetdirect.gwt.umlapi.client.analyser;

import com.objetdirect.gwt.umlapi.client.UMLDrawerException;

/**
 * @author henri, florian
 */
public class LexicalAnalyser {
    public enum LexicalFlag {
	CHAR, CHAR_DEFINED, DECIMAL, DOT_OR_DECIMAL, ESCAPED_CHAR, ESCAPED_STRING, EXPONENT, FLOAT, IDENTIFIER, INTEGER, NUMERIC, SIGN, SIGN_CONTINUED, SIGN_OR_NUMERIC, SIGNED_EXPONENT, START_DECIMAL, START_EXPONENT, STRING, UNDEFINED, VISIBILITY;
    }

    /**
     * @author henri, florian
     */
    public static class Token {
	/**
	 * 
	 */
	String content;
	/**
	 * 
	 */
	LexicalFlag type;

	/**
	 * @param type
	 * @param content
	 */
	public Token(final LexicalFlag type, final String content) {
	    this.type = type;
	    this.content = content;
	}

	/**
	 * @return the content of the token
	 * 
	 */
	public String getContent() {
	    return content;
	}

	/**
	 * @return the type of the token
	 * @see LexicalFlag
	 */
	public LexicalFlag getType() {
	    return type;
	}
    }

    int ptr;
    LexicalFlag status = LexicalFlag.UNDEFINED;
    String text;

    StringBuffer tokenStringBuffer = new StringBuffer();

    public LexicalAnalyser(final String text) {
	this.text = text;
	ptr = 0;
    }

    public Token getToken() {
	Token token = null;
	while (token == null) {
	    if (ptr >= text.length()) {
		if (tokenStringBuffer.length() > 0) {
		    token = processEOF();
		    if (token != null) {
			return token;
		    }
		    throw new UMLDrawerException("Unexpected EOF");
		}
		return null;
	    }
	    token = processNextChar();
	}
	return token;
    }

    Token consume(final LexicalFlag consumeStatus, final char c) {
	tokenStringBuffer.append(c);
	ptr++;
	final String content = tokenStringBuffer.toString();
	tokenStringBuffer = new StringBuffer();
	status = LexicalFlag.UNDEFINED;
	return new Token(consumeStatus, content);
    }

    Token ignore() {
	ptr++;
	return null;
    }

    Token inject(final LexicalFlag injectStatus) {
	final String content = tokenStringBuffer.toString();
	tokenStringBuffer = new StringBuffer();
	status = LexicalFlag.UNDEFINED;
	return new Token(injectStatus, content);
    }

    Token process(final LexicalFlag processStatus, final char c) {
	tokenStringBuffer.append(c);
	ptr++;
	status = processStatus;
	return null;
    }

    Token processEOF() {
	switch (status) {
	case VISIBILITY:
	    return inject(LexicalFlag.VISIBILITY);
	case IDENTIFIER:
	    return inject(LexicalFlag.IDENTIFIER);
	case SIGN_OR_NUMERIC:
	    return inject(LexicalFlag.SIGN);
	case DOT_OR_DECIMAL:
	    return inject(LexicalFlag.SIGN);
	case NUMERIC:
	    return inject(LexicalFlag.INTEGER);
	case DECIMAL:
	    return inject(LexicalFlag.FLOAT);
	case EXPONENT:
	    return inject(LexicalFlag.FLOAT);
	case SIGN_CONTINUED:
	    return inject(LexicalFlag.SIGN);
	}
	return null;
    }

    @SuppressWarnings("fallthrough")
    Token processNextChar() {
	final char c = text.charAt(ptr);
	switch (status) {
	case UNDEFINED:
	    if (c == ' ') {
		return ignore();
	    } else if (c == '#' || c == '+' || c == '-' || c == '~') {
		return process(LexicalFlag.VISIBILITY, c);
	    } else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_') {
		return process(LexicalFlag.IDENTIFIER, c);
	    } else if (c == '#' || c == '(' || c == ')' || c == ',' || c == '{'
		    || c == '}' || c == ':' || c == '[' || c == ']') {
		return consume(LexicalFlag.SIGN, c);
	    } else if (c == '<' || c == '>') {
		return process(LexicalFlag.SIGN_CONTINUED, c);
	    } else if (c == '+' || c == '-') {
		return process(LexicalFlag.SIGN_OR_NUMERIC, c);
	    } else if (c == '.') {
		return process(LexicalFlag.DOT_OR_DECIMAL, c);
	    } else if (c == '\'') {
		return process(LexicalFlag.CHAR, c);
	    } else if (c == '"') {
		return process(LexicalFlag.STRING, c);
	    } else if (c >= '0' && c <= '9') {
		return process(LexicalFlag.NUMERIC, c);
	    }
	    throw new UMLDrawerException("Invalid character : " + c);
	case VISIBILITY:
	    return inject(LexicalFlag.VISIBILITY);
	case IDENTIFIER:
	    if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_'
		    || c >= '0' && c <= '9') {
		return process(LexicalFlag.IDENTIFIER, c);
	    }

	    return inject(LexicalFlag.IDENTIFIER);
	case SIGN_OR_NUMERIC:
	    if (c >= '0' && c <= '9') {
		return process(LexicalFlag.NUMERIC, c);
	    } else if (c == '.') {
		return process(LexicalFlag.DECIMAL, c);
	    }

	    return inject(LexicalFlag.SIGN);
	case SIGN_CONTINUED:
	    if (c == '=') {
		return consume(LexicalFlag.SIGN, c);
	    }

	    return inject(LexicalFlag.SIGN);
	case DOT_OR_DECIMAL:
	    if (c >= '0' && c <= '9') {
		return process(LexicalFlag.START_DECIMAL, c);
	    }

	    return inject(LexicalFlag.SIGN);
	case STRING:
	    if (c == '\\') {
		return process(LexicalFlag.ESCAPED_STRING, c);
	    } else if (c == '"') {
		return consume(LexicalFlag.STRING, c);
	    }

	case ESCAPED_STRING:
	    return process(LexicalFlag.STRING, c);
	case CHAR:
	    if (c == '\\') {
		return process(LexicalFlag.ESCAPED_CHAR, c);
	    } else if (c != '\'') {
		return process(LexicalFlag.CHAR_DEFINED, c);
	    }
	    throw new UMLDrawerException("Invalid character : " + c);
	case ESCAPED_CHAR:
	    return process(LexicalFlag.CHAR_DEFINED, c);
	case CHAR_DEFINED:
	    if (c == '\'') {
		return consume(LexicalFlag.CHAR, c);
	    }
	    throw new UMLDrawerException("Invalid character : " + c);
	case NUMERIC:
	    if (c >= '0' && c <= '9') {
		return process(LexicalFlag.NUMERIC, c);
	    } else if (c == '.') {
		return process(LexicalFlag.DECIMAL, c);
	    } else if (c == 'e' || c == 'E') {
		return process(LexicalFlag.SIGNED_EXPONENT, c);
	    } else {
		return consume(LexicalFlag.INTEGER, c);
	    }
	case START_DECIMAL:
	    if (c >= '0' && c <= '9') {
		return process(LexicalFlag.DECIMAL, c);
	    }
	    throw new UMLDrawerException("Invalid character : " + c);
	case DECIMAL:
	    if (c >= '0' && c <= '9') {
		return process(LexicalFlag.DECIMAL, c);
	    } else if (c == 'e' || c == 'E') {
		return process(LexicalFlag.SIGNED_EXPONENT, c);
	    }
	    return inject(LexicalFlag.FLOAT);

	case SIGNED_EXPONENT:
	    if (c == '+' || c == '-') {
		return process(LexicalFlag.START_EXPONENT, c);
	    } else if (c >= '0' && c <= '9') {
		return process(LexicalFlag.EXPONENT, c);
	    }
	    throw new UMLDrawerException("Invalid character : " + c);
	case START_EXPONENT:
	    if (c >= '0' && c <= '9') {
		return process(LexicalFlag.EXPONENT, c);
	    }
	    throw new UMLDrawerException("Invalid character : " + c);
	case EXPONENT:
	    if (c >= '0' && c <= '9') {
		return process(LexicalFlag.EXPONENT, c);
	    }

	    return inject(LexicalFlag.FLOAT);
	}
	throw new UMLDrawerException("Invalid status : " + status);
    }
}
