/**
 * 
 */
package org.ubimix.commons.parser.xml;

/**
 * @author kotelnikov
 */
public class EntityDictionary {

    private EntityFactory fFactory;

    /**
     * 
     */
    public EntityDictionary(EntityFactory factory) {
        fFactory = factory;
    }

    protected Entity newEntityKey(String name, int code) {
        return fFactory.newEntityKey(name, code);
    }

}
