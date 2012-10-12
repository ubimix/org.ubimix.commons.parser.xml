/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.AbstractTokenizer;
import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.ICharStream.IMarker;

/**
 * @author kotelnikov
 */
public class EntityTokenizer extends AbstractTokenizer {

    private EntityFactory fFactory;

    private boolean fIgnoreUnknownEntities;

    /**
     * 
     */
    public EntityTokenizer(EntityFactory factory) {
        this(factory, true);
    }

    /**
     * 
     */
    public EntityTokenizer(EntityFactory factory, boolean ignoreUnknownEntities) {
        fFactory = factory;
        fIgnoreUnknownEntities = ignoreUnknownEntities;
    }

    protected Entity getEntityKey(boolean digit, boolean hex, String str) {
        Entity entity;
        int code = -1;
        if (digit) {
            if (hex) {
                code = Integer.parseInt(str, 16);
            } else {
                code = Integer.parseInt(str);
            }
            entity = fFactory.getEntityKeyByCode(code);
            str = null;
        } else {
            entity = fFactory.getEntityKeyByName(str);
        }
        if (entity == null && (digit || !fIgnoreUnknownEntities)) {
            entity = new Entity(str, code);
        }
        return entity;
    }

    @Override
    protected EntityToken newToken() {
        return new EntityToken();
    }

    @Override
    public EntityToken read(ICharStream stream) {
        EntityToken result = null;
        char ch = stream.getChar();
        if (ch != '&') {
            return null;
        }
        ICharStream.IMarker marker = stream.markPosition();
        try {
            Entity entity = readEntity(marker, stream);
            if (entity != null) {
                result = newToken(stream, marker);
                result.setEntityKey(entity);
            }
            return result;
        } finally {
            marker.close(result == null);
        }
    }

    private Entity readEntity(IMarker marker, ICharStream stream) {
        if (!stream.incPos()) {
            return null;
        }
        ICharStream.IPointer begin = stream.getPointer();
        char ch = stream.getChar();
        boolean hex = false;
        boolean digit = false;
        if (ch == '#') {
            digit = true;
            if (!stream.incPos()) {
                return null;
            }
            ch = stream.getChar();
            begin = stream.getPointer();
            hex = ch == 'x';
            if (hex) {
                if (!stream.incPos()) {
                    return null;
                }
                begin = stream.getPointer();
                ch = stream.getChar();
            }
            while (Character.isDigit(ch)) {
                if (!stream.incPos()) {
                    return null;
                }
                ch = stream.getChar();
            }
        } else {
            while (Character.isLetterOrDigit(ch)) {
                if (!stream.incPos()) {
                    return null;
                }
                ch = stream.getChar();
            }
        }
        ICharStream.IPointer end = stream.getPointer();
        if (begin.getPos() == end.getPos()) {
            return null;
        }
        ch = stream.getChar();
        if (ch != ';') {
            return null;
        }
        stream.incPos();
        String str = marker.getSubstring(
            begin.getPos(),
            end.getPos() - begin.getPos());
        Entity entity = getEntityKey(digit, hex, str);
        if (entity == null) {
            return null;
        }
        return entity;
    }

    public String resolveAllEntities(String str) {
        CharStream stream = new CharStream(str);
        StringBuilder buf = new StringBuilder();
        while (!stream.isTerminated()) {
            EntityToken token = read(stream);
            if (token != null) {
                Entity entity = token.getEntityKey();
                char[] ch = Character.toChars(entity.getCode());
                buf.append(ch);
            } else {
                char ch = stream.getChar();
                buf.append(ch);
                stream.incPos();
            }
        }
        return buf.toString();
    }

}