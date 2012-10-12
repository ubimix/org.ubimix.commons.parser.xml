/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.util.Map;

import junit.framework.TestCase;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.xml.utils.XmlSerializer;

/**
 * @author kotelnikov
 */
public class XmlParserTest extends TestCase {

    /**
     * @param name
     */
    public XmlParserTest(String name) {
        super(name);
    }

    protected XmlParser newXmlParser() {
        return new XmlParser();
    }

    private String parseAndSerialize(String str) {
        XmlSerializer listener = new XmlSerializer();
        listener.setSortAttributes(false);
        IXmlParser parser = newXmlParser();
        parser.parse(new CharStream(str), listener);
        return listener.toString();
    }

    public void testAttributes() {
        testAttributeWithEntities("a&#x27;b", "a'b");
        testAttributeWithEntities("a'b", "a'b");
        testParser("<div prop=\"value\"/>", "<div prop='value'></div>");
        testParser("<div prop=\"a'b\"/>", "<div prop='a&#x27;b'></div>");

    }

    private void testAttributeWithEntities(String attr, final String control) {
        IXmlParser parser = newXmlParser();
        parser.parse(
            new CharStream("<div prop=\"" + attr + "\"/>"),
            new XmlListener() {
                @Override
                public void beginElement(
                    String tagName,
                    Map<String, String> attributes,
                    Map<String, String> namespaces) {
                    String value = attributes.get("prop");
                    assertEquals(control, value);
                }
            });
    }

    private void testParser(String str) {
        testParser(str, str);
    }

    private void testParser(String str, String control) {
        String test1 = parseAndSerialize(str);
        assertEquals(control, test1);
        String test2 = parseAndSerialize(test1);
        assertEquals(control, test2);
    }

    public void testSerializeDeserialize() {
        testParser("<div />", "<div></div>");
        testParser("<div>This is a text</div>", "<div>This is a text</div>");
        testParser("<div>This is a text", "<div>This is a text</div>");
        testParser("", "");
        testParser("<a/>", "<a></a>");
        testParser("    <div />   ", "<div></div>");
        testParser("<root>"
            + "<a xmlns='foo'><x></x><y></y></a>"
            + "<a xmlns:n='bar'><n:x></n:x><n:y></n:y></a>"
            + "</root>");
        testParser(
            "<feed xmlns='http://www.w3.org/2005/Atom' />",
            "<feed xmlns='http://www.w3.org/2005/Atom'></feed>");
        testParser("<a><b><c><d><e><f>Text</f></e></d></c></b></a>");
        testParser("<a><b>Text</b><c>Text</c><d>Text</d><e>Text</e><f>Text</f></a>");
        testParser(""
            + "<html>"
            + "<head>"
            + "<title>Hello, world</title>"
            + "</head>"
            + "<body>"
            + "<p class='first'>A new paragraph</p>"
            + "</body>"
            + "</html>");
        testParser(
            ""
                + "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
                + " \n"
                + "    <title>Example Feed</title>\n"
                + "    <subtitle>A subtitle.</subtitle>\n"
                + "    <link href='http://example.org/feed/' rel='self' />\n"
                + "    <link href='http://example.org/' />\n"
                + "    <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>\n"
                + "    <updated>2003-12-13T18:30:02Z</updated>\n"
                + "    <author>\n"
                + "        <name>John Doe</name>\n"
                + "        <email>johndoe@example.com</email>\n"
                + "    </author>\n"
                + " \n"
                + "    <entry>\n"
                + "        <title>Atom-Powered Robots Run Amok</title>\n"
                + "        <link href='http://example.org/2003/12/13/atom03' />\n"
                + "        <link rel='alternate' type='text/html' href='http://example.org/2003/12/13/atom03.html'/>\n"
                + "        <link rel='edit' href='http://example.org/2003/12/13/atom03/edit'/>\n"
                + "        <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n"
                + "        <updated>2003-12-13T18:30:02Z</updated>\n"
                + "        <summary>Some text.</summary>\n"
                + "        <category term='robots'/>\n"
                + "        <category term='test' label='Test Label' scheme='http://example.org/ns/tags/'/>\n"
                + "        <content type='html' xmlns='http://www.w3.org/1999/xhtml'>Robot-generated content.</content>\n"
                + "    </entry>\n"
                + " \n"
                + "</feed>",
            ""
                + "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
                + " \n"
                + "    <title>Example Feed</title>\n"
                + "    <subtitle>A subtitle.</subtitle>\n"
                + "    <link href='http://example.org/feed/' rel='self'></link>\n"
                + "    <link href='http://example.org/'></link>\n"
                + "    <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>\n"
                + "    <updated>2003-12-13T18:30:02Z</updated>\n"
                + "    <author>\n"
                + "        <name>John Doe</name>\n"
                + "        <email>johndoe@example.com</email>\n"
                + "    </author>\n"
                + " \n"
                + "    <entry>\n"
                + "        <title>Atom-Powered Robots Run Amok</title>\n"
                + "        <link href='http://example.org/2003/12/13/atom03'></link>\n"
                + "        <link rel='alternate' type='text/html' href='http://example.org/2003/12/13/atom03.html'></link>\n"
                + "        <link rel='edit' href='http://example.org/2003/12/13/atom03/edit'></link>\n"
                + "        <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n"
                + "        <updated>2003-12-13T18:30:02Z</updated>\n"
                + "        <summary>Some text.</summary>\n"
                + "        <category term='robots'></category>\n"
                + "        <category term='test' label='Test Label' scheme='http://example.org/ns/tags/'></category>\n"
                + "        <content type='html' xmlns='http://www.w3.org/1999/xhtml'>Robot-generated content.</content>\n"
                + "    </entry>\n"
                + " \n"
                + "</feed>");
    }

}
