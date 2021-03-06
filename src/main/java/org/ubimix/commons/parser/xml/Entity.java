/**
 * 
 */
package org.ubimix.commons.parser.xml;

public class Entity {

    private final int fCode;

    private final String fName;

    public Entity(String name, int code) {
        fName = name;
        fCode = code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity o = (Entity) obj;
        return (fCode == o.fCode)
            && ((fName == null || o.fName == null) ? fName == o.fName : fName
                .equals(o.fName));
    }

    public String getChars() {
        if (fCode >= 0) {
            char[] chars = Character.toChars(fCode);
            return new String(chars);
        } else {
            return toString();
        }
    }

    /**
     * @return the code
     */
    public int getCode() {
        return fCode;
    }

    /**
     * @return the name
     */
    public String getName() {
        return fName;
    }

    @Override
    public int hashCode() {
        return fCode;
    }

    @Override
    public String toString() {
        String result = "";
        if (fCode >= 0) {
            result = "&#" + fCode + ";";
        } else {
            result = "&" + fName + ";";
        }
        // switch (fCode) {
        // case 38:
        // return "&amp;";
        // case 155:
        // return "&gt;";
        // case 139:
        // return "&lt;";
        // default:
        // return "&#" + fCode + ";";
        // }
        return result;
    }

}