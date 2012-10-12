/**
 * 
 */
package org.ubimix.commons.parser.xml;

import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.StreamToken;

/**
 * @author kotelnikov
 */
public class AttrToken extends StreamToken {

    private String fName;

    private ICharStream.IPointer fNameBegin;

    private ICharStream.IPointer fNameEnd;

    private String fResolvedValue;

    private String fValue;

    private ICharStream.IPointer fValueBegin;

    private ICharStream.IPointer fValueEnd;

    /**
     * @return the name
     */
    public String getName() {
        return fName;
    }

    /**
     * @return the nameBegin
     */
    public ICharStream.IPointer getNameBegin() {
        return fNameBegin;
    }

    /**
     * @return the nameEnd
     */
    public ICharStream.IPointer getNameEnd() {
        return fNameEnd;
    }

    public String getResolvedValue() {
        return fResolvedValue;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return fValue;
    }

    /**
     * @return the valueBegin
     */
    public ICharStream.IPointer getValueBegin() {
        return fValueBegin;
    }

    /**
     * @return the valueEnd
     */
    public ICharStream.IPointer getValueEnd() {
        return fValueEnd;
    }

    void setName(
        ICharStream.IPointer begin,
        ICharStream.IPointer end,
        String str) {
        fNameBegin = begin;
        fNameEnd = end;
        fName = str;
    }

    public void setResolvedValue(String value) {
        fResolvedValue = value;
    }

    void setValue(
        ICharStream.IPointer begin,
        ICharStream.IPointer end,
        String str) {
        fValueBegin = begin;
        fValueEnd = end;
        fValue = str;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + fName + "=" + fValue + ")";
    }

}