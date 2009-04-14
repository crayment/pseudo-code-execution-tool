/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 *
 * @author Abhinav
 */
public class RecentFiles extends Observable implements Serializable
{

    ArrayList<String> files;
    int MAX_SIZE = 10;

    public RecentFiles()
    {
        files = new ArrayList<String>();
    }

    public ArrayList<String> getFiles()
    {
        return files;
    }

    public void addFile(String name)
    {
        if(files.contains(name)) return;
        
        if (files.size() <= MAX_SIZE)
        {
            files.add(0, name);
        }else{
            files.remove(files.size()-1);
            files.add(0, name);
        }
    }

    public void removeFile(String name)
    {
        if (files.contains(name))
        {
            files.remove(name);
        }
    }
    
    /*==========================================================================
     * NOTE: Do not change this value of [serialVersionUID]  in future revisions unless you are
     * knowingly making changes to the class which will render it incompatible
     * with old serialized objects
     *==========================================================================*/
    private static final long serialVersionUID = 5370268949607398956L;
}
