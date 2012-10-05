/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.AbstractTokenizer;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.ICharStream.IMarker;
import org.ubimix.commons.parser.ICharStream.IPointer;
import org.ubimix.commons.parser.StreamToken;

public class TagTokenizer extends AbstractTokenizer {

    public final static TagTokenizer INSTANCE = new TagTokenizer();

    private AttrTokenizer fAttrTokenizer;

    public TagTokenizer() {
        this(AttrTokenizer.INSTANCE);
    }

    public TagTokenizer(AttrTokenizer attrTokenizer) {
        fAttrTokenizer = attrTokenizer;
    }

    protected boolean isSpecialSymbol(char ch) {
        boolean result = false;
        switch (ch) {
            case '!':
            case '"':
            case '#':
            case '$':
            case '%':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
                // case '/':
            case ':':
            case ';':
                // case '<':
            case '=':
                // case '>':
            case '?':
            case '@':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case '{':
            case '|':
            case '}':
            case '~':
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected StreamToken newToken() {
        return new TagToken();
    }

    @Override
    public StreamToken read(ICharStream stream) {
        char ch = stream.getChar();
        if (ch != '<') {
            return null;
        }

        TagToken result = null;
        ICharStream.IMarker marker = stream.markPosition();
        try {
            ICharStream.IPointer begin = stream.getPointer();
            if (!stream.incPos()) {
                return null;
            }
            ch = stream.getChar();
            boolean open = true;
            boolean close = false;
            if (ch == '/') {
                open = false;
                close = true;
                if (!stream.incPos()) {
                    return result;
                }
            }

            ICharStream.IPointer nameBegin = stream.getPointer();
            skipName(stream);
            ICharStream.IPointer nameEnd = stream.getPointer();
            if (nameBegin.getPos() == nameEnd.getPos()) {
                return null;
            }
            String name = getString(marker, nameBegin, nameEnd);

            List<AttrToken> attributes = null;
            while (true) {
                ch = stream.getChar();
                if (ch == '/') {
                    if (!stream.incPos()) {
                        break;
                    }
                    ch = stream.getChar();
                    if (ch == '>') {
                        close = true;
                        stream.incPos();
                        break;
                    }
                } else if (ch == '>') {
                    stream.incPos();
                    break;
                } else if (ch == '<') {
                    return null;
                }

                if (skipSpaces(stream)) {
                    continue;
                }

                if (skipSpecialSymbols(stream)) {
                    continue;
                }

                AttrToken next = fAttrTokenizer.read(stream);
                if (next != null) {
                    if (attributes == null) {
                        attributes = new ArrayList<AttrToken>();
                    }
                    attributes.add(next);
                    continue;
                }

                if (!stream.incPos()) {
                    break;
                }
            }
            ICharStream.IPointer end = stream.getPointer();
            result = newToken(begin, end, getString(marker, begin, end));
            result.init(open, close);
            result.setName(nameBegin, nameEnd, name);
            result.setAttributes(attributes);
            return result;
        } finally {
            marker.close(result == null);
        }
    }

    private void skipName(ICharStream stream) {
        char ch = stream.getChar();
        if (!Character.isLetter(ch)) {
            return;
        }
        while (stream.incPos()) {
            ch = stream.getChar();
            if (!Character.isLetterOrDigit(ch) && ch != ':') {
                break;
            }
        }
    }

    private boolean skipSpaces(ICharStream stream) {
        boolean result = false;
        while (Character.isSpaceChar(stream.getChar())) {
            result = true;
            if (!stream.incPos()) {
                break;
            }
        }
        return result;
    }

    private boolean skipSpecialSymbols(ICharStream stream) {
        boolean result = false;
        while (isSpecialSymbol(stream.getChar())) {
            result = true;
            if (!stream.incPos()) {
                break;
            }
        }
        return result;
    }

}