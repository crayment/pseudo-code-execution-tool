/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author crayment
 */
public class SafeFileChooser extends JFileChooser
{
    public SafeFileChooser()
    {
        super();
    }

    public SafeFileChooser(String baseDir)
    {
        super(baseDir);
    }


    @Override
    public void approveSelection()
    {
        if(!validateFile(this.getSelectedFile()))
        {
            //show some error message
            return;//the FileChooser wont close until and unless u call super.approveSelection()
        }
        super.approveSelection();
    }

    private boolean validateFile(File file)
    {
        //do your validation here
        if (file.exists())
        {
            if (JOptionPane.showConfirmDialog(null, "File exists, overwrite?", "Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                return true;
            return false;
        }
        else
            return true;
    }
}