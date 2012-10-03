/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.base.DelimitedTextTokenizer;

/**
 * @author kotelnikov
 */
public class CDATATokenizer extends DelimitedTextTokenizer {

    public static final CDATATokenizer INSTANCE = new CDATATokenizer();

    public CDATATokenizer() {
        super("<![CDATA[", "]]>", false);
    }

    @Override
    protected StreamToken newToken() {
        return new CDATAToken();
    }
}