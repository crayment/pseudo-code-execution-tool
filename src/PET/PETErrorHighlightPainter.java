/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET;

import java.awt.Color;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
/**
 *
 * @author crayment
 */
public class PETErrorHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

    private static PETErrorHighlightPainter instance;

    private PETErrorHighlightPainter(Color color) {
        super(color);
    }

    public static PETErrorHighlightPainter getInstance()
    {
        if(instance == null)
            instance = new PETErrorHighlightPainter(new Color(240, 0, 0));
        return instance;
    }


    // Removes only our private highlights
    public static void removeHighlights(JTextComponent textComp) {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();

        for (int i=0; i<hilites.length; i++) {
            if (hilites[i].getPainter() instanceof PETErrorHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
}
