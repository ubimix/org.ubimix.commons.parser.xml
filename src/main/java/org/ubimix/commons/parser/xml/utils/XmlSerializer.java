/**
 * 
 */
package org.ubimix.commons.parser.xml.utils;

/**
 * @author kotelnikov
 */
public class XmlSerializer extends AbstractXmlSerializer {

    protected StringBuilder fBuilder = new StringBuilder();

    /**
     * 
     */
    public XmlSerializer() {
    }

    @Override
    protected void print(String string) {
        fBuilder.append(string);
    }

    public void reset() {
        fBuilder.delete(0, fBuilder.length());
    }

    @Override
    public String toString() {
        return fBuilder.toString();
    }

}
