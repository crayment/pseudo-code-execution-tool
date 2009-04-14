/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.model;

import PET.parser.Parser;

/**
 *
 * @author crayment
 */
public class Highlight {
    private int startIndex;
    private int endIndex;
    private int line;

    public Highlight(int startIndex, int length, int line)
    {
        this.startIndex = startIndex;
        this.endIndex = length;
        this.line = line;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }


    /**
     * @return the length
     */
    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Highlight) {
            Highlight h = (Highlight) obj;

            if((h.getStartIndex() == getStartIndex()) && (h.getEndIndex() == getEndIndex())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.startIndex;
        hash = 59 * hash + this.endIndex;
        hash = 59 * hash + this.getLineNumber();
        return hash;
    }


    @Override
    public String toString() {
        String highlight = Parser.getProgram().substring(startIndex, endIndex);

        return "Highlight "+startIndex+":"+endIndex+":"+getLineNumber()+":|"+highlight+"|";
    }

    /**
     * @return the line
     */
    public int getLineNumber() {
        return line;
    }
}
