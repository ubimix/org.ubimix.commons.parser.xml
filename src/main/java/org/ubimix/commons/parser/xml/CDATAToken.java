/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.StreamToken;

/**
 * @author kotelnikov
 */
public class CDATAToken extends StreamToken {

    public String getCDATAValue() {
        String token = getText();
        if (token.startsWith("<![CDATA[")) {
            token = token.substring("<![CDATA[".length());
            if (token.endsWith("]]>")) {
                token = token.substring(0, token.length() - "]]>".length());
            }
        }
        return token;
    }

}
