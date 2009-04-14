/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.validator;

import java.util.Hashtable;
import java.util.Observable;
import java.util.Enumeration;

/**
 * Represents the programs variables.  Allows you to define set and get
 * variables.
 * @author crayment, iap
 */
public final class Variables extends Observable {


    /**
     * The instance of the singleton
     */
    private static Variables instance;

    /**
     * private constructor to defeat instatiation
     */
    private Variables(){}

    /**
     * Get the static instance of the singleton
     * @return the static instance
     */
    public synchronized static Variables getInstance()
    {
        if(Variables.instance == null)
            Variables.instance = new Variables();
        return Variables.instance;
    }

    /**
     * The Hashtable used to store the variables in. The keys are the string
     * refernces for the variables and the values are the values of the variables.
     */
    private static Hashtable variables = new Hashtable();

    /**
     * Defines a variable and asigns a value to it.  The type of variable is
     * inferred from the class of the value passed.
     * @param name The reference name of the variable.
     * @param value The value to assign to the variable.
     */
    public static void define(String name, Object value)
    {
        Variables.variables.put(name, value);

        Variables.getInstance().setChanged();
        Variables.getInstance().notifyObservers(name);
    }

   

    /**
     * Gets the value of the variable.
     * @param name The reference name of the variable to get.
     * @return The value stored in the variable.
     * @throws PET.model.ParserException if there is no variable defined with
     * the given <code>name</code>.
     */
    public static Object get(String name)
    {
        return  Variables.variables.get(name);
    }

    /**
     * Checks to see if a variable is defined.
     * @param name The reference name of the variable to check.
     * @return <code>true</code> if the reference name has been defined.
     */
    public static boolean isDefined(String name)
    {
        return  Variables.variables.containsKey(name);
    }



    /**
     * Undefines all variables.
     */
    public static void clearAllVariables()
    {
        for(Enumeration e = Variables.variables.keys(); e.hasMoreElements();)
        {
            String name = (String)e.nextElement();
            Variables.variables.remove(name);

            Variables.getInstance().setChanged();
            Variables.getInstance().notifyObservers(name);
        }
    }
}
