/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 *
 * @author crayment
 */
public final class TextAreaOutputStream  extends OutputStream{

    private final JTextArea textArea;
    private final StringBuilder sb = new StringBuilder();
    public TextAreaOutputStream(final JTextArea textArea) {
        this.textArea = textArea;
    }
    @Override
    public void write(int b) throws IOException {
        if (b == '\r') { return; }
        if (b == '\n') {
            textArea.append(sb.toString());
            sb.setLength(0);
        }
        sb.append((char)b);

        textArea.append(sb.toString());
        sb.setLength(0);
    }

}


