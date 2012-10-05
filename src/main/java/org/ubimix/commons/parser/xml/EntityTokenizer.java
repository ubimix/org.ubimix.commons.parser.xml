/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.AbstractTokenizer;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.ICharStream.IMarker;
import org.ubimix.commons.parser.StreamToken;

/**
 * @author kotelnikov
 */
public class EntityTokenizer extends AbstractTokenizer {

    private EntityReader fReader;

    public EntityTokenizer(EntityFactory factory) {
        this(new EntityReader(factory));
    }

    public EntityTokenizer(EntityReader reader) {
        fReader = reader;
    }

    @Override
    protected StreamToken newToken() {
        return new EntityToken();
    }

    @Override
    public StreamToken read(ICharStream stream) {
        char ch = stream.getChar();
        if (ch != '&') {
            return null;
        }
        EntityToken result = null;
        ICharStream.IMarker marker = stream.markPosition();
        try {
            Entity entity = fReader.readEntity(stream);
            if (entity != null) {
                result = newToken(stream, marker);
                result.setEntityKey(entity);
            }
            return result;
        } finally {
            marker.close(result == null);
        }
    }

}