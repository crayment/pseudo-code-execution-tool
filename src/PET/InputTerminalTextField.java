/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JTextField;


/**
 *
 * @author crayment
 */
public class InputTerminalTextField extends JTextField{

    String PreText = "$>";

    @Override
    public Insets getInsets()
    {
        return getInsets(new Insets(0, 0, 0, 0));
    }

    @Override
    public Insets getInsets(Insets insets)
    {
        insets = super.getInsets(insets);
        insets.left += widthOfPreText();
        return insets;
    }


    private int widthOfPreText() {
        int result = getFontMetrics(getFont()).stringWidth(PreText);
        return result;
    }


    @Override
    public void paintComponent(Graphics g)
    {

        super.paintComponent(g);
        
        Insets insets = getInsets();
        
        FontMetrics fm = g.getFontMetrics();
        int y = fm.getAscent() + insets.top;

        g.setColor(getForeground());
        g.drawString("$>", 0, y);


    }

}
