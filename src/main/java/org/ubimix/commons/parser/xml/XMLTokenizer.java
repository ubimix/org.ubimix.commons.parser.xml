/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.CompositeTokenizer;
import org.ubimix.commons.parser.text.TextTokenizer;

/**
 * This composite tokenizer is used to detect structural components of
 * XML/XHTML/HTML documents. Note that text elements (words, spaces and special
 * symbols) are NOT recognized by this tokenizer. For these elements the
 * TextTokenizer should be used. Recognized entities:
 * <ul>
 * <li>Elements and their attributes. Elements could be opening, closing or
 * empty.</li>
 * <li>Comments</li>
 * <li>Prolog like <code>&lt;!DOCTYPE html PUBLIC
 * "-//W3C//DTD XHTML 1.0 Transitional//EN"
 * "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;<code></li>
 * <li>Processing instructions like <code>&lt;?xml version='1.0'?&gt;</code> or
 * <code>&lt;?xml-stylesheet href="transform.xsl" type="text/xsl"?&gt;</code></li>
 * <li>CDATA blocks like</li>
 * </ul>
 * 
 * @author kotelnikov
 */
public class XMLTokenizer extends CompositeTokenizer {

    public static XMLTokenizer getFullXMLTokenizer() {
        EntityFactory entityFactory = new EntityFactory();
        return getFullXMLTokenizer(entityFactory);
    }

    public static XMLTokenizer getFullXMLTokenizer(EntityFactory entityFactory) {
        // HTML entities
        EntityTokenizer entityReader = new EntityTokenizer(entityFactory, false);
        XMLTokenizer tokenizer = new XMLTokenizer(entityReader);
        tokenizer.addTokenizer(new TextTokenizer());
        return tokenizer;
    }

    private AttrTokenizer T_ATTR;

    private EntityTokenizer T_ENTITY;

    private TagTokenizer T_TAGS;

    /**
     * @param xmlDict
     * @param entityReader
     */
    public XMLTokenizer(EntityTokenizer entityTokenizer) {
        T_ENTITY = entityTokenizer;
        T_ATTR = new AttrTokenizer(entityTokenizer);
        T_TAGS = new TagTokenizer(T_ATTR);
        addTokenizer(CommentTokenizer.INSTANCE);
        addTokenizer(CDATATokenizer.INSTANCE);
        addTokenizer(ProcessingInstructionTokenizer.INSTANCE);
        addTokenizer(PrologTokenizer.INSTANCE);
        addTokenizer(T_TAGS);
        addTokenizer(T_ENTITY);
    }

    public AttrTokenizer getAttrTokenizer() {
        return T_ATTR;
    }

    public EntityTokenizer getEntityTokenizer() {
        return T_ENTITY;
    }

    public TagTokenizer getTagTokenizer() {
        return T_TAGS;
    }

}
