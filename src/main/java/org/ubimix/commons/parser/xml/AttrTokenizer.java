/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.AbstractTokenizer;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.StreamToken;

/**
 * @author kotelnikov
 */
public class AttrTokenizer extends AbstractTokenizer {

    public static final AttrTokenizer INSTANCE = new AttrTokenizer();

    public final static boolean skipQuotedText(ICharStream stream, char esc) {
        boolean result = false;
        char openChar = stream.getChar();
        if (openChar == '\'' || openChar == '"') {
            result = true;
            boolean escaped = false;
            while (stream.incPos()) {
                if (escaped) {
                    escaped = false;
                    continue;
                }
                char ch = stream.getChar();
                if (ch == openChar) {
                    stream.incPos();
                    result = true;
                    break;
                } else if (ch == esc) {
                    escaped = true;
                }
            }
        }
        return result;
    }

    protected char getEscapeSymbol() {
        return '\\';
    }

    protected boolean isValueChar(char ch) {
        return !Character.isSpaceChar(ch) && ch != '>' && ch != '<';
    }

    @Override
    protected StreamToken newToken() {
        return new AttrToken();
    }

    @Override
    public AttrToken read(ICharStream stream) {
        AttrToken result = null;
        ICharStream.IMarker marker = stream.markPosition();
        try {
            ICharStream.IPointer nameBegin = stream.getPointer();
            skipName(stream);
            ICharStream.IPointer nameEnd = stream.getPointer();
            if (nameBegin.getPos() == nameEnd.getPos()) {
                return null;
            }
            String name = getString(marker, nameBegin, nameEnd);
            skipSpaces(stream);
            ICharStream.IPointer valueBegin = stream.getPointer();
            ICharStream.IPointer valueEnd = stream.getPointer();
            if (stream.getChar() == '=') {
                stream.incPos();
                skipSpaces(stream);
                valueBegin = valueEnd = stream.getPointer();
                skipValue(stream);
                valueEnd = stream.getPointer();
            }
            String value = getString(marker, valueBegin, valueEnd);
            result = newToken(nameBegin, valueEnd, getString(marker, valueEnd));
            result.setName(nameBegin, nameEnd, name);
            result.setValue(valueBegin, valueEnd, value);
            return result;
        } finally {
            marker.close(result == null);
        }
    }

    private void skipName(ICharStream stream) {
        char ch = stream.getChar();
        while (Character.isLetterOrDigit(ch) || ch == ':' || ch == '-') {
            if (!stream.incPos()) {
                break;
            }
            ch = stream.getChar();
        }
    }

    private void skipSpaces(ICharStream stream) {
        for (; Character.isSpaceChar(stream.getChar()); stream.incPos()) {
        }
    }

    private boolean skipValue(ICharStream stream) {
        char esc = getEscapeSymbol();
        boolean result = skipQuotedText(stream, esc);
        if (!result) {
            char ch = stream.getChar();
            while (isValueChar(ch)) {
                result = true;
                if (!stream.incPos()) {
                    break;
                }
                ch = stream.getChar();
            }
        }
        return result;
    }

}