
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * A storage spot and handler for settings.  Implemented recursively, I.E. this class has a vector of children,
 * and when a change is made to the parent, it is also made to all of the children.
 *
 * Example usage: a parent SettingsHanlder object, mainWindow, that has IOTerminal and Codewindow as children.
 * The overhead projector text is to small, so the mainWindow has its fontSize changed, changing the
 * fontsize of both CodeWindow and IOTerminal.  IOTerminal's black color makes it hard to see, so
 * mainWindow.getChildByName("IOTerminal").setBackground("green"); is called to set the IOterminal
 * to green, keeping the code window white.
 * /

package PET;

/**
 *
 * @author Blair
 */
public class SettingsHandler {


     private String name = "";

     private int fontSize = 12;
     private String font = "Times New Roman";
     private String backgroundColour = "white";
     private String textColour = "black";

     private Object targetElement = null;

    private  Vector <SettingsHandler> children = new  Vector();


    //Empty constructor.  Leaves things at the above default values.
    public SettingsHandler()
    {

    }

    //Children-setting constructor.  Note that it does NOT SET THE CHILDREN TO THE VALUES OF THE PARENT.
    public SettingsHandler(Vector newChildren)
    {
        this.children = newChildren;
    }

    //User-specified constructor.  Sets the internal settings to the user-specified values.
    public SettingsHandler(int newFontSize, String newFont, String newBackgroundColour, String newTextColour)
    {
        this.setFontSize(newFontSize);
        this.setFont(newFont);
        this.setBackgroundColour(newBackgroundColour);
        this.setTextColour(newTextColour);
    }

    //Set this, and all it's children, to have background colour newBackgroundColour
    public void setBackgroundColour(String newBackgroundColour) {

        this.backgroundColour = newBackgroundColour;


        int length = this.children.size();
        for(int i = 0; i<= length; i++)
        {
            this.children.elementAt(i).setBackgroundColour(newBackgroundColour);
        }

    }

    //Set this, and all it's children, to have font newFont.
    public void setFont(String newFont) {

        this.font = newFont;


        int length = this.children.size();
        for(int i = 0; i<= length; i++)
        {
            this.children.elementAt(i).setFont(newFont);
        }
    }

    //Set this, and all it's children, to have fontsize newFontSize.
    public void setFontSize(int newFontSize) {

        this.fontSize = newFontSize;


        int length = this.children.size();
        for(int i = 0; i<= length; i++)
        {
            this.children.elementAt(i).setFontSize(newFontSize);
        }
    }

    //Set this, and all it's children, to have textcolour newTextColour.
    public void setTextColour(String newTextColour) {

        this.textColour = newTextColour;


        int length = this.children.size();
        for(int i = 0; i<= length; i++)
        {
            this.children.elementAt(i).setTextColour(newTextColour);
        }
    }

    //Set the targetElement to target.
    public void setTargetElement(Object target)
    {
        this.targetElement = target;
    }

    public void setName(String theName)
    {
        this.name = theName;
    }


    //Accessor functions
    public String getFont()
    {
        return this.font;
    }

    public int getFontSize()
    {
        return this.fontSize;
    }

    public String getBackgroundColour()
    {
        return this.backgroundColour;
    }

    public String getTextColour()
    {
        return this.textColour;
    }

    public Object getTargetElement()
    {
        return this.targetElement;
    }
    
    public String getName()
    {
        return this.name;
    }

    //Accessor function that searches the children for a child of a specific name, or null if not found.
    //Note that is only searches DIRECT children, not children-of-children.
    public SettingsHandler getChildByName(String theName)
    {
        int length = this.children.size();
        for(int i = 0; i<= length; i++)
        {
            if ( this.children.elementAt(i).getName().equals(theName) )
            {
                return this.children.elementAt(i);
            }
        }
        return null;
    }

    //This function is commented out and written basically in pseudocode due to my inexperience with Swing and best practices.
    //It's intended purpose is to actually apply the settings contained in this SettingsHandler, and all it's children, by
    //applying the settings to their targetElements (the actual swing elements).

    //If you do implement this function, you may want to call it at the end of every function that edits
    //any settings, to make the SettingsHandler and Swing output self-updating, rather than having to call
    //applySettings in the main function.  If so, consider making the method private.

    //Note that this need not be implemented if not wanted: you can simply get the settings with the
    //accessor functions for use in the main program, but that defeats the purpose as far as I can see.
    /*
    public void applySettings()
    {
        this.targetElement.setFont(this.font)
           //etc...

        int length = this.children.size();
        for(int i = 0; i<= length; i++)
        {
            this.children.elementAt(i).applySettings();
        }
    }
    */



}
