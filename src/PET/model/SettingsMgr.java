package PET.model;

import java.io.*;

/**
 * This calss is responsible for handling storage and retrival of
 * seriziable objects.
 * @author Abhinav
 */
public class SettingsMgr
{

    private String _userHome;
    private static SettingsMgr SMgr;

     /**
     * constructor does nothing but prevent multiple instances
     * required for singleton
     */
    public  SettingsMgr()
    {
 
        _userHome = System.getProperty("user.home");

    }

    /**
     * get the instance of the singleton class
     * @return the instance
     */
    public static SettingsMgr getInstance(){
        if(SMgr==null){
            SMgr = new SettingsMgr();
            return SMgr;
        }else{
            return SMgr;
        }

    }

    /**
     * This function is used for reading Seriziable Objects
     * @param type Name of the SERIZIABLE class to be retrived
     * @return Object containg the saved information.
     * @throws java.lang.ClassNotFoundException
     * @throws java.io.IOException
     */
    public Object readObject(String type) throws ClassNotFoundException, IOException
    {
        String settingsFile;
        ObjectInputStream in;
        Object obj = null;

        String settingFN = String.format("/.%s.ser", type);
        settingsFile = (_userHome + settingFN);
        File f = new File(settingsFile);

        if (f.exists() == true)
        {
            in = new ObjectInputStream(new FileInputStream(f));
            obj = new Object();
            obj = in.readObject();
        }
        return obj;
    }

    /**
     * This funtion writes seriziable objects to user's home directory.
     * @param obj Seriziable object to be written.
     * @throws java.io.IOException
     */
    public void writeObject(Object obj) throws IOException
    {
        String settingsFile;
        ObjectOutputStream out;

        String settingFN = String.format("/.%s.ser", obj.getClass().getName());
        settingsFile = (_userHome + settingFN);
        File f = new File(settingsFile);

        if (f.exists() == false)
        {
            f.createNewFile();
        }
        out = new ObjectOutputStream(new FileOutputStream(f));
        if (obj != null)
        {
            out.writeObject(obj);
        }
    }

    /**
     * removes the persistent associated with the specified object
     * @param obj OPbject to be removed from the disk
     * @throws IOException 
     */
    public void removeSettings(Object obj) throws IOException
    {
        String settingsFile;    
        String settingFN = String.format("/.%s.ser", obj.getClass().getName());
        settingsFile = (_userHome + settingFN);
        File f = new File(settingsFile);
        if (f.exists() == true)
        {
            f.delete();
        }     
    }
}
