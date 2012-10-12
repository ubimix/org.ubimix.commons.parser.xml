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

    private EntityTokenizer fEntityTokenizer;

    public AttrTokenizer(EntityTokenizer entityTokenizer) {
        fEntityTokenizer = entityTokenizer;
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
            StringBuilder valueBuf = new StringBuilder();
            if (stream.getChar() == '=') {
                stream.incPos();
                skipSpaces(stream);
                valueBegin = valueEnd = stream.getPointer();
                skipValue(stream, valueBuf);
                valueEnd = stream.getPointer();
            }
            String value = getString(marker, valueBegin, valueEnd);
            result = newToken(nameBegin, valueEnd, getString(marker, valueEnd));
            result.setName(nameBegin, nameEnd, name);
            result.setValue(valueBegin, valueEnd, value);
            result.setResolvedValue(valueBuf.toString());
            return result;
        } finally {
            marker.close(result == null);
        }
    }

    private boolean readEntity(ICharStream stream, StringBuilder valueBuf) {
        boolean result = false;
        EntityToken entityToken = fEntityTokenizer.read(stream);
        if (entityToken != null) {
            result = true;
            Entity entity = entityToken.getEntityKey();
            valueBuf.append(entity.getChars());
        }
        return result;
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

    private boolean skipQuotedText(
        ICharStream stream,
        char esc,
        StringBuilder valueBuf) {
        boolean result = false;
        char openChar = stream.getChar();
        if (openChar == '\'' || openChar == '"') {
            result = true;
            boolean escaped = false;
            stream.incPos();
            while (!stream.isTerminated()) {
                if (escaped) {
                    char ch = stream.getChar();
                    valueBuf.append(ch);
                    escaped = false;
                    stream.incPos();
                    continue;
                }
                if (readEntity(stream, valueBuf)) {
                    continue;
                } else {
                    char ch = stream.getChar();
                    if (ch == openChar) {
                        stream.incPos();
                        result = true;
                        break;
                    } else if (ch == esc) {
                        escaped = true;
                    } else {
                        valueBuf.append(ch);
                    }
                    stream.incPos();
                }
            }
        }
        return result;
    }

    private void skipSpaces(ICharStream stream) {
        for (; Character.isSpaceChar(stream.getChar()); stream.incPos()) {
        }
    }

    private boolean skipValue(ICharStream stream, StringBuilder valueBuf) {
        char esc = getEscapeSymbol();
        boolean result = skipQuotedText(stream, esc, valueBuf);
        if (!result) {
            char ch = stream.getChar();
            while (!stream.isTerminated()) {
                if (readEntity(stream, valueBuf)) {
                    result = true;
                } else {
                    ch = stream.getChar();
                    if (!isValueChar(ch)) {
                        break;
                    }
                    result = true;
                    valueBuf.append(ch);
                    if (!stream.incPos()) {
                        break;
                    }
                }
            }
        }
        return result;
    }
}