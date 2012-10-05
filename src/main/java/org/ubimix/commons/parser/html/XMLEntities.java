/**
 * 
 */
package org.ubimix.commons.parser.html;

import org.ubimix.commons.parser.xml.Entity;
import org.ubimix.commons.parser.xml.EntityDictionary;
import org.ubimix.commons.parser.xml.EntityFactory;

/**
 * @author kotelnikov
 */
public class XMLEntities extends EntityDictionary {

    /** "amp" - U+0026 (38) XML 1.0 ampersand */
    public final Entity S_AMP = newEntityKey("amp", 38);

    /** "apos" /* U+0027 (39) XML 1.0 apostrophe (= apostrophe-quote) */
    public final Entity S_APOS = newEntityKey("apos", 39);

    /** U+003E (62) XML 1.0 greater-than sign */
    public final Entity S_GT = newEntityKey("gt", 62);

    /** U+003C (60) XML 1.0 less-than sign */
    public final Entity S_LT = newEntityKey("lt", 60);

    /** "quot" - XML 1.0 double quotation mark */
    public final Entity S_QUOT = newEntityKey("quot", 34);

    /**
     * @param factory
     */
    public XMLEntities(EntityFactory factory) {
        super(factory);
    }
}
