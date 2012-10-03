package org.ubimix.commons.parser.html;

import org.ubimix.commons.parser.xml.EntityFactory;

/**
 * @author kotelnikov
 */
public class XHTMLEntities {

    public final XHTMLCharactersEntities CHARS;

    public final XHTMLSpecialEntities SPECIALS;

    public final XHTMLSymbolsEntities SYMBOLS;

    public XHTMLEntities(EntityFactory entityFactory) {
        CHARS = new XHTMLCharactersEntities(entityFactory);
        SYMBOLS = new XHTMLSymbolsEntities(entityFactory);
        SPECIALS = new XHTMLSpecialEntities(entityFactory);
    }

}
