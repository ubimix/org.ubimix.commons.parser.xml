/**
 * 
 */
package org.ubimix.commons.parser.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.CharStream.Pointer;
import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.ITokenizer.StreamToken;
import org.ubimix.commons.parser.text.TextDict;

/**
 * @author kotelnikov
 */
public class XMLFormatter {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        XMLFormatter formatter = new XMLFormatter();
        Class<XMLFormatter> cls = XMLFormatter.class;
        String html = TestUtil.readResource(cls, "Wikipedia-France.html");
        String result = formatter.format(html);
        File file = new File("./tmp/Wikipedia-France.highlighted.html");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(result);
        writer.close();
    }

    /**
     * 
     */
    public XMLFormatter() {
        // TODO Auto-generated constructor stub
    }

    private boolean check(StreamToken token, Class<?> type) {
        return type.isInstance(token);
    }

    private String escape(String str) {
        return str
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }

    private String format(String html) {
        ITokenizer tokenizer = XMLTokenizer.getFullXMLTokenizer();
        StringBuilder first = new StringBuilder();
        StringBuilder second = new StringBuilder();
        first
            .append("<html>"
                + "<head>"
                + "<style>\n"
                + ".container .lines { color: gray; border-right: 1px solid gray; }\n"
                + ".container .lines * { text-align: right; }\n"
                + ".container .code { }\n"
                + ""
                + ".tag { color: black; }\n"
                + ".tag .name { color: maroon; }\n"
                + ".tag .attr { color: black; }\n"
                + ".tag .value { color: blue; }\n"
                + ".comment { color: green; }\n"
                + ".entity { color: red; }\n"
                + ".prolog { color: silver; }\n"
                + ".processing { color: silver; }\n"
                + "</style>"
                + "</head>"
                + "<body>");
        int lastLine = 0;
        CharStream stream = new CharStream(html);
        while (true) {
            StreamToken token = tokenizer.read(stream);
            if (token == null) {
                break;
            }
            lastLine = token.getEnd().line;
            format(second, token);
        }
        first.append("<table class='container'><tr valign='top'>");
        first.append("<td class='lines'><pre>");
        for (int i = 1; i <= lastLine + 1; i++) {
            first.append("" + i).append("\n");
        }
        first.append("</pre></td><td class='code'><pre>");
        first.append(second);
        first.append("</pre></td>");
        first.append("</tr></table>");
        first.append("</body></html>");
        return first.toString();
    }

    private void format(StringBuilder buf, StreamToken token) {
        String str = token.getText();
        if (token instanceof TagToken) {
            TagToken tag = (TagToken) token;
            buf.append("<span class='tag'>");
            if (tag.isOpen() && tag.isClose()) {
                buf.append("&lt;");
                printTagContent(buf, tag);
                buf.append("/&gt;");
            } else if (tag.isOpen()) {
                buf.append("&lt;");
                printTagContent(buf, tag);
                buf.append("&gt;");
            } else {
                buf.append("&lt;/");
                printTagContent(buf, tag);
                buf.append("&gt;");
            }
            buf.append("</span>");
        } else if (token instanceof EntityToken) {
            buf
                .append("&amp;")
                .append("<span class='entity'>")
                .append(str.substring(1, str.length() - 1))
                .append("</span>")
                .append(";");
        } else {
            str = escape(str);
            if (check(token, TextDict.SpecialSymbolsToken.class)
                || check(token, TextDict.WordToken.class)
                || check(token, TextDict.SpecialSymbolsToken.class)
                || check(token, TextDict.NewLineToken.class)) {
                buf.append(str);
            } else {
                String key = getTokenKey(token);
                buf.append("<span class='" + key + "'>");
                buf.append(str);
                buf.append("</span>");
            }
        }
    }

    private String getTokenKey(StreamToken token) {
        String name = token.getClass().getName();
        int idx = name.lastIndexOf('.');
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        idx = name.lastIndexOf('$');
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        if (name.length() > 1) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        if (name.endsWith("Token")) {
            name = name.substring(0, name.length() - "Token".length());
        }
        return name;
    }

    private void printTagContent(StringBuilder buf, TagToken tag) {
        String name = tag.getName();
        buf.append("<b class='name'>").append(name).append("</b>");
        String str = tag.getText();
        Pointer start = tag.getBegin();
        Pointer prev = tag.getNameEnd();
        List<AttrToken> attributes = tag.getAttributes();
        for (AttrToken attr : attributes) {
            Pointer curr = attr.getBegin();
            buf.append(str
                .substring(prev.pos - start.pos, curr.pos - start.pos));

            buf.append("<b class='attr'>");
            buf.append(attr.getName());
            buf.append("</b>");

            prev = attr.getNameEnd();
            curr = attr.getValueBegin();
            buf.append(str
                .substring(prev.pos - start.pos, curr.pos - start.pos));

            buf.append("<b class='value'>");
            buf.append(escape(attr.getValue()));
            buf.append("</b>");

            prev = attr.getEnd();
        }
    }
}
