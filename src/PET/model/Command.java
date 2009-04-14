/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.model;

/**
 *
 * @author crayment
 */
public class Command {
    private String line;
    private Highlight highlight;

    public Command(String line)
    {
        this(line, null);
    }

    public Command(String commandString, Highlight highlight)
    {
        this.line = commandString;
        this.highlight = highlight;
    }

    /**
     * @return the line
     */
    public String getLine() {
        return line;
    }


    /**
     * @return the highlight
     */
    public Highlight getHighlight() {
        return highlight;
    }

    /**
     * @param highlight the highlight to set
     */
    public void setHighlight(Highlight highlight) {
        this.highlight = highlight;
    }


    @Override
    public String toString() {
        return this.getLine() + " " + this.highlight;
    }

}
