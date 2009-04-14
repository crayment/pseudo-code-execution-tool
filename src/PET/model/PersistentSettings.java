/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.model;

import java.io.*;
import java.awt.Color;

/**
 * This is a seriziable class responsible for handling customiziable user settings
 * for GUI appearance.
 * @author Abhinav
 */
public final class PersistentSettings implements Serializable
{
    private int fontSize;
    private String fontType;
    private Color codeBGColor;
    private Color terminalBGColor;
    private Color codeTextColor;
    private Color terminalTextColor;
    private Color outputBGColor;
    private Color outputTextColor;
    private Color codeRunningBGColor;
    private Color codeRunningFontColor;
    private Color codeLineColor;
    private Color codeHighlightColor;

    public PersistentSettings()
    {
        this.setCodeBGColor(Color.WHITE);
        this.setCodeTextColor(Color.BLACK);
        this.setCodeLineColor(new Color(232,232,246));
        this.setCodeRunningBGColor(new Color(211,211,211));
        this.setCodeRunningFontColor(Color.BLACK);

        this.setFontSize(14);
        this.setFontType("Monospaced");

        this.setTerminalBGColor(Color.BLACK);
        this.setTerminalTextColor(Color.GREEN);
    }

    public final Color getCodeBGColor()
    {
        return codeBGColor;
    }

    public final void setCodeBGColor(Color codeBGColor)
    {
        this.codeBGColor = codeBGColor;
    }

    public final Color getCodeTextColor()
    {
        return codeTextColor;
    }

    public final void setCodeTextColor(Color codeTextColor)
    {
        this.codeTextColor = codeTextColor;
    }

    public final int getFontSize()
    {
        return fontSize;
    }

    public final void setFontSize(int fontSize)
    {
        this.fontSize = fontSize;

    }

    public final String getFontType()
    {
        return fontType;
    }

    public final void setFontType(String fontType)
    {
        this.fontType = fontType;

    }

    public final Color getOutputBGColor()
    {
        return outputBGColor;
    }

    public final void setOutputBGColor(Color outputBGColor)
    {
        this.outputBGColor = outputBGColor;
    }

    public final Color getOutputTextColor()
    {
        return outputTextColor;
    }

    public final void setOutputTextColor(Color outputTextColor)
    {
        this.outputTextColor = outputTextColor;
    }

    public final Color getTerminalBGColor()
    {
        return terminalBGColor;
    }

    public final void setTerminalBGColor(Color terminalBGColor)
    {
        this.terminalBGColor = terminalBGColor;

    }

    public final Color getTerminalTextColor()
    {
        return terminalTextColor;
    }

    public final void setTerminalTextColor(Color terminalTextColor)
    {
        this.terminalTextColor = terminalTextColor;

    }
    /*==========================================================================
     * NOTE: Do not change this value of [serialVersionUID]  in future revisions unless you are
     * knowingly making changes to the class which will render it incompatible
     * with old serialized objects
     *==========================================================================*/
    private static final long serialVersionUID = 5370268949607398955L;

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final PersistentSettings other = (PersistentSettings) obj;
        if (this.fontSize != other.fontSize)
        {
            return false;
        }
        if ((this.fontType == null) ? (other.fontType != null) : !this.fontType.equals(other.fontType))
        {
            return false;
        }
        if (this.codeBGColor != other.codeBGColor && (this.codeBGColor == null || !this.codeBGColor.equals(other.codeBGColor)))
        {
            return false;
        }
        if (this.terminalBGColor != other.terminalBGColor && (this.terminalBGColor == null || !this.terminalBGColor.equals(other.terminalBGColor)))
        {
            return false;
        }
        if (this.codeTextColor != other.codeTextColor && (this.codeTextColor == null || !this.codeTextColor.equals(other.codeTextColor)))
        {
            return false;
        }
        if (this.terminalTextColor != other.terminalTextColor && (this.terminalTextColor == null || !this.terminalTextColor.equals(other.terminalTextColor)))
        {
            return false;
        }
        if (this.outputBGColor != other.outputBGColor && (this.outputBGColor == null || !this.outputBGColor.equals(other.outputBGColor)))
        {
            return false;
        }
        if (this.outputTextColor != other.outputTextColor && (this.outputTextColor == null || !this.outputTextColor.equals(other.outputTextColor)))
        {
            return false;
        }
        if (this.codeLineColor != other.codeLineColor && (this.codeLineColor == null || !this.codeLineColor.equals(other.codeLineColor)))
        {
            return false;
        }
        if (this.codeRunningBGColor != other.codeRunningBGColor && (this.codeRunningBGColor == null || !this.codeRunningBGColor.equals(other.codeRunningBGColor)))
        {
            return false;
        }
        if (this.codeRunningFontColor != other.codeRunningFontColor && (this.codeRunningFontColor == null || !this.codeRunningFontColor.equals(other.codeRunningFontColor)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 37 * hash + this.fontSize;
        hash = 37 * hash + (this.fontType != null ? this.fontType.hashCode() : 0);
        hash = 37 * hash + (this.codeBGColor != null ? this.codeBGColor.hashCode() : 0);
        hash = 37 * hash + (this.terminalBGColor != null ? this.terminalBGColor.hashCode() : 0);
        hash = 37 * hash + (this.codeTextColor != null ? this.codeTextColor.hashCode() : 0);
        hash = 37 * hash + (this.terminalTextColor != null ? this.terminalTextColor.hashCode() : 0);
        hash = 37 * hash + (this.outputBGColor != null ? this.outputBGColor.hashCode() : 0);
        hash = 37 * hash + (this.outputTextColor != null ? this.outputTextColor.hashCode() : 0);
        hash = 37 * hash + (this.codeLineColor != null ? this.codeLineColor.hashCode() : 0);
        hash = 37 * hash + (this.codeRunningBGColor != null ? this.codeRunningBGColor.hashCode() : 0);
        hash = 37 * hash + (this.codeRunningFontColor != null ? this.codeRunningFontColor.hashCode() : 0);
        return hash;
    }

    /**
     * @return the codeRunningBGColor
     */
    public Color getCodeRunningBGColor() {
        return codeRunningBGColor;
    }

    /**
     * @param codeRunningBGColor the codeRunningBGColor to set
     */
    public void setCodeRunningBGColor(Color codeRunningBGColor) {
        this.codeRunningBGColor = codeRunningBGColor;
    }

    /**
     * @return the codeRunningFontColor
     */
    public Color getCodeRunningFontColor() {
        return codeRunningFontColor;
    }

    /**
     * @param codeRunningFontColor the codeRunningFontColor to set
     */
    public void setCodeRunningFontColor(Color codeRunningFontColor) {
        this.codeRunningFontColor = codeRunningFontColor;
    }

    /**
     * @return the codeLineColor
     */
    public Color getCodeLineColor() {
        return codeLineColor;
    }

    /**
     * @param codeLineColor the codeLineColor to set
     */
    public void setCodeLineColor(Color codeLineColor) {
        this.codeLineColor = codeLineColor;
    }

    /**
     * @return the codeHighlightColor
     */
    public Color getCodeHighlightColor() {
        return codeHighlightColor;
    }

    /**
     * @param codeHighlightColor the codeHighlightColor to set
     */
    public void setCodeHighlightColor(Color codeHighlightColor) {
        this.codeHighlightColor = codeHighlightColor;
    }
}
