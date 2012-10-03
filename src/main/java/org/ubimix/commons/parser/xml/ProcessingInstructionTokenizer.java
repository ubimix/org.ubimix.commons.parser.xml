/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.base.DelimitedTextTokenizer;

/**
 * @author kotelnikov
 */
public class ProcessingInstructionTokenizer extends DelimitedTextTokenizer {

    public static final ProcessingInstructionTokenizer INSTANCE = new ProcessingInstructionTokenizer();

    /**
     * 
     */
    public ProcessingInstructionTokenizer() {
        super("<?", "?>", false);
    }

    @Override
    protected StreamToken newToken() {
        return new ProcessingInstructionToken();
    }

}
