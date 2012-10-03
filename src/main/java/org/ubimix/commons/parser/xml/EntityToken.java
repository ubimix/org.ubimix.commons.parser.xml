/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.ITokenizer.StreamToken;

/**
 * @author kotelnikov
 */
public class EntityToken extends StreamToken {

    private Entity fEntity;

    /**
     * @return the entityKey
     */
    public Entity getEntityKey() {
        return fEntity;
    }

    /**
     * @param entity the entityKey to set
     */
    public void setEntityKey(Entity entity) {
        fEntity = entity;
    }

}
