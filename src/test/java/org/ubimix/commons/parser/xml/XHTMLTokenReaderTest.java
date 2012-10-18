/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.commons.parser.CompositeTokenizer;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.StringBufferCharStream;
import org.ubimix.commons.parser.text.TextTokenizer;

/**
 * @author kotelnikov
 */
public class XHTMLTokenReaderTest extends TestCase {

    private Boolean fPrintOnScreen;

    private int fTokenCounter;

    public XHTMLTokenReaderTest(String name) {
        super(name);
    }

    public AttrTokenizer newAttrTokenizer() {
        EntityFactory entityFactory = new EntityFactory();
        EntityTokenizer entityTokenizer = new EntityTokenizer(entityFactory);
        return new AttrTokenizer(entityTokenizer);
    }

    protected ICharStream newStream(String str) {
        // return new CharStream(str);
        return new StringBufferCharStream(str);
    }

    protected boolean printOnScreen() {
        if (fPrintOnScreen == null) {
            boolean result = false;
            String str = System.getProperty("printOnScreen");
            if (str != null) {
                str = str.trim().toLowerCase();
                result = "ok".equals(str)
                    || "yes".equals(str)
                    || "1".equals(str);
            }
            fPrintOnScreen = result;
        }
        return fPrintOnScreen;
    }

    protected void printToken(StreamToken token) {
        if (!printOnScreen()) {
            return;
        }
        String s = token.getText();
        s = s
            .replaceAll("\\\\", "\\\\")
            .replaceAll("\\t", "\\\\t")
            .replaceAll("\\r\\n", "\\\\n")
            .replaceAll("\\n", "\\\\n");
        fTokenCounter++;
        System.out.println(fTokenCounter + ")\t" + token + "\t'" + s + "'");
    }

    @Override
    protected void setUp() throws Exception {
        fTokenCounter = 0;
    }

    public void test() throws IOException {
        // public void testXMLTokenizer() throws IOException {
        {
            CompositeTokenizer tokenizer = new CompositeTokenizer();
            tokenizer.addTokenizer(ProcessingInstructionTokenizer.INSTANCE);
            tokenizer.addTokenizer(PrologTokenizer.INSTANCE);
            tokenizer.addTokenizer(new TextTokenizer());
            test(tokenizer, "<!a>", "<!a>");
        }
        {
            CompositeTokenizer tokenizer = new CompositeTokenizer();
            tokenizer.addTokenizer(PrologTokenizer.INSTANCE);
            tokenizer.addTokenizer(ProcessingInstructionTokenizer.INSTANCE);
            tokenizer.addTokenizer(new TextTokenizer());
            test(tokenizer, "<!a>", "<!a>");
        }
        {
            CompositeTokenizer tokenizer = new CompositeTokenizer();
            tokenizer.addTokenizer(PrologTokenizer.INSTANCE);
            tokenizer.addTokenizer(ProcessingInstructionTokenizer.INSTANCE);
            tokenizer.addTokenizer(new TextTokenizer());
            test(tokenizer, "<!a>", "<!a>");
        }

        {
            EntityFactory entityFactory = new EntityFactory();
            EntityTokenizer entityTokenizer = new EntityTokenizer(
                entityFactory,
                false);
            CompositeTokenizer tokenizer = new CompositeTokenizer();
            // tokenizer.addTokenizer(CommentTokenizer.INSTANCE);
            // tokenizer.addTokenizer(CDATATokenizer.INSTANCE);
            // tokenizer.addTokenizer(ProcessingInstructionTokenizer.INSTANCE);
            tokenizer.addTokenizer(PrologTokenizer.INSTANCE);
            // tokenizer.addTokenizer(entityTokenizer);
            tokenizer.addTokenizer(new TagTokenizer(new AttrTokenizer(
                entityTokenizer)));
            tokenizer.addTokenizer(new TextTokenizer());
            test(tokenizer, "<!a>", "<!a>");
            test(tokenizer, "<!a><b c='?y'/>", "<!a>", "<b c='?y'/>");
        }
        {
            EntityFactory entityFactory = new EntityFactory();
            EntityTokenizer entityTokenizer = new EntityTokenizer(
                entityFactory,
                false);
            CompositeTokenizer tokenizer = new CompositeTokenizer();
            // tokenizer.addTokenizer(CommentTokenizer.INSTANCE);
            // tokenizer.addTokenizer(CDATATokenizer.INSTANCE);
            tokenizer.addTokenizer(ProcessingInstructionTokenizer.INSTANCE);
            tokenizer.addTokenizer(PrologTokenizer.INSTANCE);
            tokenizer.addTokenizer(entityTokenizer);
            tokenizer.addTokenizer(new TagTokenizer(new AttrTokenizer(
                entityTokenizer)));
            tokenizer.addTokenizer(new TextTokenizer());
            test(tokenizer, "<!a>", "<!a>");
        }

        testXMLTokenizer("<!a>", "[<!a>]");
        testXMLTokenizer("<!a><b c='?y'/>", "[<!a>][<b c='?y'/>]");
        testXMLTokenizer("", "");
        // Prolog
        testXMLTokenizer("<!a><b c='x?y'/>", "[<!a>][<b c='x?y'/>]");
        testXMLTokenizer("<!a><link ref='x?y'/>", "[<!a>][<link ref='x?y'/>]");

        testXMLTokenizer(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> \n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://bits.wikimedia.org/w/extensions/UsabilityInitiative/css/vector/jquery-ui-1.7.2.css?1.7.2\"/> \n"
                + "",
            "[<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">][ ][\n]"
                + "[<link rel=\"stylesheet\" type=\"text/css\" href=\"http://bits.wikimedia.org/w/extensions/UsabilityInitiative/css/vector/jquery-ui-1.7.2.css?1.7.2\"/>][ ][\n]");
        testXMLTokenizer(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
                + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">",
            "[<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
                + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">]");

        // Parsing instructions
        testXMLTokenizer("<?xml version=\"1.0\"?>", "[<?xml version=\"1.0\"?>]");
        testXMLTokenizer(
            "<?xml version=\"1.0\"?>after",
            "[<?xml version=\"1.0\"?>][after]");
        testXMLTokenizer(
            "before<?xml version=\"1.0\"?>after",
            "[before][<?xml version=\"1.0\"?>][after]");
        testXMLTokenizer(
            "<?xml version=\"1.0\"?>\n"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
                + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">",
            "[<?xml version=\"1.0\"?>][\n]"
                + "[<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
                + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">]");

        // Parsing instructions and prologs
        testXMLTokenizer(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + "<!DOCTYPE greeting [\n"
                + "  <!ELEMENT greeting (#PCDATA)>\n"
                + "]>\n"
                + "<greeting>Hello, world!</greeting>",
            "[<?xml version=\"1.0\" encoding=\"UTF-8\" ?>][\n]"
                + "[<!DOCTYPE greeting [\n"
                + "  <!ELEMENT greeting (#PCDATA)>\n"
                + "]>][\n]"
                + "[<greeting>][Hello][,][ ][world][!][</greeting>]");

        // Special characters
        testXMLTokenizer(".", "[.]");
        testXMLTokenizer("a.b?c*d", "[a][.][b][?][c][*][d]");
        // testXMLTokenizer("–*", "[�][�][�][*]");
        // testXMLTokenizer("<p>–", "[<p>][�][�][�]");
        testXMLTokenizer("<p>&b&c&#160;</p>", "[<p>][&][b][&][c][&#160;][</p>]");

        // CDATA blocks
        testXMLTokenizer(""
            + "<root>\n"
            + "    <![CDATA[<greeting>Hello, world!</greeting>]]>\n"
            + "</root>", ""
            + "[<root>][\n]"
            + "[    ][<![CDATA[<greeting>Hello, world!</greeting>]]>][\n]"
            + "[</root>]");

        // A "complex" tokenization example
        testXMLTokenizer(
            ""
                + "<!-- This is a comment -->\n"
                + "<html>\n"
                + "<head>\n"
                + "   <title>Hello, world!</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "   <h1>I am here!</h1>"
                + "   <p>This is a new paragraph &nbsp; &nbsp;</p>\n"
                + "<p>An another paragraph <br /> with \r\n"
                + " a line break.</p>\n"
                + "<hr \n"
                + "     style='border: 1px solid red; \r\n"
                + " margin: 1em 0;'/>\n"
                + "</body>\n"
                + "</html>",
            ""
                + "[<!-- This is a comment -->][\n]"
                + "[<html>][\n]"
                + "[<head>][\n]"
                + "[   ][<title>][Hello][,][ ][world][!][</title>][\n]"
                + "[</head>][\n]"
                + "[<body>][\n]"
                + "[   ][<h1>][I][ ][am][ ][here][!][</h1>]"
                + "[   ][<p>][This][ ][is][ ][a][ ][new][ ][paragraph][ ][&nbsp;][ ][&nbsp;][</p>][\n]"
                + "[<p>][An][ ][another][ ][paragraph][ ][<br />][ ][with][ ][\r\n]"
                + "[ ][a][ ][line][ ][break][.][</p>][\n]"
                + "[<hr \n"
                + "     style='border: 1px solid red; \r\n"
                + " margin: 1em 0;'/>][\n]"
                + "[</body>][\n]"
                + "[</html>]");
    }

    private void test(ITokenizer tokenizer, String string, String... control) {
        ICharStream stream = newStream(string);
        for (String c : control) {
            StreamToken token = tokenizer.read(stream);
            assertNotNull(token);
            assertEquals(c, token.getText());
        }
        StreamToken token = tokenizer.read(stream);
        assertNull(token);
    }

    public void testAttributeTokenizer() throws IOException {
        testAttributeTokenizer("a ", "a", "");
        testAttributeTokenizer("a=b ", "a", "b");
        testAttributeTokenizer("a = b ", "a", "b");
        testAttributeTokenizer("a = b c d", "a", "b");
        testAttributeTokenizer("a = 'b c d'", "a", "'b c d'");
        testAttributeTokenizer("a = '  b c d  '  ", "a", "'  b c d  '");
        testAttributeTokenizer("a:b:c = '  b c d  '  ", "a:b:c", "'  b c d  '");
    }

    private void testAttributeTokenizer(String str, String name, String value) {
        ICharStream stream = newStream(str);
        AttrTokenizer attrTokenizer = newAttrTokenizer();
        StreamToken token = attrTokenizer.read(stream);
        assertNotNull(token);
        assertTrue(token instanceof AttrToken);
        AttrToken attr = (AttrToken) token;
        assertEquals(name, attr.getName());
        assertEquals(value, attr.getValue());
    }

    public void testTagTokenizer() {
        testTagTokenizer("<tag a>", "tag", "a", "");
        testTagTokenizer("<tag a b>", "tag", "a", "", "b", "");
        testTagTokenizer("<tag *=* a>", "tag", "a", "");
        testTagTokenizer("<tag *=* a * b >", "tag", "a", "", "b", "");
        testTagTokenizer("<tag *|= a=b>", "tag", "a", "b");
        testTagTokenizer("<tag *|= a=b = c >", "tag", "a", "b", "c", "");
        testTagTokenizer("<tag *|= a=b = c &*- >", "tag", "a", "b", "c", "");
        testTagTokenizer("<tag a=b = c>", "tag", "a", "b", "c", "");
        testTagTokenizer("<tag c &*- = d >", "tag", "c", "", "d", "");

        testTagTokenizer("<tag a b>", "tag", "a", "", "b", "");
        testTagTokenizer("<tag a b >", "tag", "a", "", "b", "");
        testTagTokenizer("<tag a b c>", "tag", "a", "", "b", "", "c", "");
        testTagTokenizer("<tag a b c >", "tag", "a", "", "b", "", "c", "");
        testTagTokenizer("<tag a c   d >", "tag", "a", "", "c", "", "d", "");
        testTagTokenizer("<tag a c &  d >", "tag", "a", "", "c", "", "d", "");
        testTagTokenizer("<tag a c & = d >", "tag", "a", "", "c", "", "d", "");
        testTagTokenizer("<tag a c &* = d >", "tag", "a", "", "c", "", "d", "");
        testTagTokenizer("<tag a c &*- = d >", "tag", "a", "", "c", "", "d", "");
        testTagTokenizer(
            "<tag a=b = c &*- = d >",
            "tag",
            "a",
            "b",
            "c",
            "",
            "d",
            "");
        testTagTokenizer(
            "<tag *|= a=b = c &*- = d >",
            "tag",
            "a",
            "b",
            "c",
            "",
            "d",
            "");
        testTagTokenizer(
            "<tag *|= a=b = c &*- = d >",
            "tag",
            "a",
            "b",
            "c",
            "",
            "d",
            "");
        testTagTokenizer(
            "<tag *|= a=b = c &*- = d = e f >",
            "tag",
            "a",
            "b",
            "c",
            "",
            "d",
            "e",
            "f",
            "");

        testTagTokenizer("< tag >", null);
        testTagTokenizer(" tag", null);
        testTagTokenizer("<notatag <tag2", null);

        // Bad tokens
        testTagTokenizer("<tag *|= >", "tag");
        testTagTokenizer(
            "<tag *|= a=b = c &*- = d = e f >",
            "tag",
            "a",
            "b",
            "c",
            "",
            "d",
            "e",
            "f",
            "");

        // Normal tokens
        testTagTokenizer("<tag>", "tag");
        testTagTokenizer("<tag", "tag");
        testTagTokenizer("<tag a>", "tag", "a", "");
        testTagTokenizer("<tag a=b>", "tag", "a", "b");
        testTagTokenizer("<tag a='b'>", "tag", "a", "'b'");
        testTagTokenizer(
            "<tag a = 'b' c = 'Hello, world'>",
            "tag",
            "a",
            "'b'",
            "c",
            "'Hello, world'");
        testTagTokenizer("<tag a b c   ", "tag", "a", "", "b", "", "c", "");
        testTagTokenizer(
            "<tag a b='Hello, world!'>",
            "tag",
            "a",
            "",
            "b",
            "'Hello, world!'");
        testTagTokenizer(
            "<tag a b c d= e  ",
            "tag",
            "a",
            "",
            "b",
            "",
            "c",
            "",
            "d",
            "e");
        testTagTokenizer("<tag a = 'b'  ", "tag", "a", "'b'");
    }

    private void testTagTokenizer(
        String str,
        String tag,
        String... attrsControl) {
        ICharStream stream = newStream(str);
        AttrTokenizer attrTokenizer = newAttrTokenizer();
        TagTokenizer tokenizer = new TagTokenizer(attrTokenizer);
        StreamToken token = tokenizer.read(stream);
        if (tag == null) {
            assertNull(token);
            return;
        }
        assertNotNull(token);
        assertTrue(token instanceof TagToken);
        TagToken tagToken = (TagToken) token;
        assertEquals(tag, tagToken.getName());
        List<AttrToken> attrTokens = tagToken.getAttributes();
        assertEquals(attrsControl.length / 2, attrTokens.size());
        for (int i = 0; i < attrsControl.length;) {
            AttrToken attrToken = attrTokens.get(i / 2);
            assertNotNull(attrToken);
            String name = attrsControl[i++];
            String value = attrsControl[i++];
            assertEquals(name, attrToken.getName());
            assertEquals(value, attrToken.getValue());
        }
    }

    private void testXMLTokenizer(String str) {
        testXMLTokenizer(str, null);
    }

    private void testXMLTokenizer(String str, String control) {
        ITokenizer tokenizer = XMLTokenizer.getFullXMLTokenizer();
        StringBuilder first = new StringBuilder();
        StringBuilder second = new StringBuilder();
        ICharStream stream = newStream(str);
        while (true) {
            StreamToken token = tokenizer.read(stream);
            if (token == null) {
                break;
            }
            printToken(token);
            first.append(token.getText());
            if (control != null) {
                second.append("[").append(token.getText()).append("]");
            }
        }
        assertEquals(str, first.toString());
        if (control != null) {
            assertEquals(control, second.toString());
        }
    }

    public void testXMLTokenizerFile() throws IOException {
        String str;
        // str = readResource("Wikipedia-Parsing.html");
        Class<? extends XHTMLTokenReaderTest> cls = getClass();
        // str = TestUtil.readResource(cls, "Wikipedia-France.html");
        str = TestUtil.readResource(cls, "Wikipedia-United_States.html");
        long start = System.currentTimeMillis();
        testXMLTokenizer(str);
        long end = System.currentTimeMillis();
        System.out.println("Parsed in " + (end - start) + "ms");
    }
}
