/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.ITokenizer;

/**
 * @author kotelnikov
 */
public interface IXmlTokenizer extends ITokenizer {

    EntityTokenizer getEntityTokenizer();

}
