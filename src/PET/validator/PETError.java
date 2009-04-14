/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.validator;

import PET.model.Highlight;

/**
 *
 * @author crayment
 */
public class PETError
{

    public String funcitonName = null;
    private String msg;
    private Highlight highlight;
    private String helpFile;

    public PETError(String msg, Highlight h, String helpFile) {
        setMsg(msg);
        setHighlight(h);
        setHelpFile(helpFile);
    }

    public PETError(String msg, Highlight h)
    {
        setMsg(msg);
        setHighlight(h);
    }


    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    @Override
    public String toString()
    {
        return "Error: " + msg;
    }

    /**
     * @return the h
     */
    public Highlight getHighlight() {
        return highlight;
    }

    /**
     * @param h the h to set
     */
    public void setHighlight(Highlight h) {
        this.highlight = h;
    }

    /**
     * @return the helpFile
     */
    public String getHelpFile() {
        return helpFile;
    }

    /**
     * @param helpFile the helpFile to set
     */
    public void setHelpFile(String helpFile) {
        this.helpFile = helpFile;
    }
}
