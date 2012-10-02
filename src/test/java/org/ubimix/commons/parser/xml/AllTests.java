package org.ubimix.commons.parser.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
            "Test for org.ubimix.commons.parser.xml");
        // $JUnit-BEGIN$

        // XML/XHTML
        suite.addTestSuite(StreamSequenceTokenizerTest.class);
        suite.addTestSuite(XHTMLTokenReaderTest.class);
        suite.addTestSuite(XmlParserTest.class);
        // $JUnit-END$
        return suite;
    }
}
