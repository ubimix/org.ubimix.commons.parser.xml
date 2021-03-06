/**
 * 
 */
package org.ubimix.commons.parser.xml.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.xml.Entity;
import org.ubimix.commons.parser.xml.XmlListener;

/**
 * @author kotelnikov
 */
public abstract class AbstractXmlSerializer extends XmlListener {

    private boolean fSortAttributes;

    /**
     * 
     */
    public AbstractXmlSerializer() {
    }

    /**
     * @see org.ubimix.model.xml.IXmlListener#beginElement(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    @Override
    public void beginElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        print("<");
        printName(name);
        Set<String> keys = attributes.keySet();
        String[] array = keys.toArray(new String[keys.size()]);
        if (sortAttributes()) {
            Arrays.sort(array);
        }
        for (String attrName : array) {
            print(" ");
            String attrValue = attributes.get(attrName);
            printName(attrName);
            print("='");
            printValue(attrValue);
            print("'");
        }

        keys = namespaces.keySet();
        array = keys.toArray(new String[namespaces.size()]);
        if (sortAttributes()) {
            Arrays.sort(array);
        }
        for (String prefix : array) {
            print(" ");
            String namespace = namespaces.get(prefix);
            print("xmlns");
            if (!"".equals(prefix)) {
                print(":" + prefix);
            }
            print("='");
            printValue(namespace + "");
            print("'");
        }
        print(">");
    }

    /**
     * @see org.ubimix.model.xml.IXmlListener#endElement(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    @Override
    public void endElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        print("</");
        printName(name);
        print(">");
    }

    /**
     * Returns the escaped string.
     * 
     * @param str the string to escape
     * @param escapeQuots if this flag is <code>true</code> then "'" and "\""
     *        symbols also will be escaped
     * @return the escaped string.
     */
    public String escapeXmlString(String str, boolean escapeQuots) {
        if (str == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        int len = str.length();
        int[] i = { 0 };
        while (i[0] < len) {
            char ch = str.charAt(i[0]);
            String entity = readEntity(str, i);
            if (entity != null) {
                buf.append(entity);
            } else {
                if (ch == '>'
                    || ch == '&'
                    || ch == '<'
                    || (escapeQuots && (ch == '\'' || ch == '"'))) {
                    buf.append("&#x" + Integer.toHexString(ch) + ";");
                } else {
                    buf.append(ch);
                }
                i[0]++;
            }
        }
        return buf.toString();
    }

    /**
     * @see org.ubimix.model.xml.IXmlListener#onCDATA(java.lang.String)
     */
    @Override
    public void onCDATA(String content) {
        print("<![CDATA[");
        print(content);
        print("]]>");
    }

    @Override
    public void onComment(String commentContent) {
        print("<!--");
        print(commentContent);
        print("-->");
    }

    @Override
    public void onEntity(Entity entity) {
        String str = "";
        int code = entity.getCode();
        if (code > 0) {
            str = Integer.toHexString(code);
            if (str.length() < 2) {
                str = "0" + str;
            }
            str = "&#x" + str + ";";
        } else {
            str = entity.toString();
        }
        print(str);
    }

    /**
     * @see org.ubimix.model.xml.IXmlListener#onText(java.lang.String)
     */
    @Override
    public void onText(String text) {
        String str = escapeXmlString(text, false);
        print(str);
    }

    protected abstract void print(String string);

    protected void printName(String name) {
        print(name);
    }

    protected void printValue(String value) {
        value = escapeXmlString(value, true);
        print(value);
    }

    private String readEntity(CharSequence stream, int[] pos) {
        int idx = pos[0];
        int len = stream.length();
        char ch = stream.charAt(idx);
        if (ch != '&') {
            return null;
        }
        String entity = null;
        idx++;
        if (idx >= len) {
            return null;
        }
        int begin = idx;
        ch = stream.charAt(idx);
        boolean digit = false;
        if (ch == '#') {
            digit = true;
            idx++;
            if (idx >= len) {
                return null;
            }
            ch = stream.charAt(idx);
            begin = idx;
            while (Character.isDigit(ch)) {
                idx++;
                if (idx >= len) {
                    return null;
                }
                ch = stream.charAt(idx);
            }
        }
        int end = idx;
        if (begin == end) {
            return null;
        }
        ch = stream.charAt(idx);
        if (ch != ';') {
            return null;
        }
        idx++;
        if (digit) {
            CharSequence str = stream.subSequence(begin, end);
            entity = "&#" + str + ";";
        }
        if (entity == null) {
            return null;
        }
        if (entity != null) {
            pos[0] = idx;
        }
        return entity;
    }

    public void setSortAttributes(boolean sortAttributes) {
        fSortAttributes = sortAttributes;
    }

    public boolean sortAttributes() {
        return fSortAttributes;
    }

}
