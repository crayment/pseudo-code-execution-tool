/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.model;

import java.util.Hashtable;
import PET.Constants;
import PET.PETApp;
import PET.parser.ParserException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Enumeration;
import java.util.Vector;

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
     * Defines a <code>Integer</code> variable with the default value 0.
     * @param name The reference name of the variable.
     * @throws PET.model.ParserException if there is already a variable with the
     * same reference name.
     */
    public static void defineNumber(String name) throws ParserException
    {
        // Integers default to 0
        define(name, new Integer(0));
    }

    /**
     * Defines a <code>Boolean</code> variable with the default value false.
     * @param name The reference name of the variable.
     * @throws PET.model.ParserException if there is already a variable with the
     * same reference name.
     */
    public static void defineCond(String name) throws ParserException
    {
        // Booleans default to false
        define(name, new Boolean(false));
    }

    /**
     * Defines a <code>Integer[]</code> variable with the default size 10.
     * @param name The reference name of the variable.
     * @throws PET.model.ParserException if there is already a variable with the
     * same reference name.
     */
    public static void defineArray(String name, int size) throws ParserException
    {
        Integer[] array = new Integer[size];
        for(int i=0;i<array.length; i++)
            array[i] = 0;
        define(name, array);
    }
    
    /**
     * Defines a <code>String</code> variable defaulting to an empty string.
     * @param name The name of the created string.
     * @throws PET.model.ParserException if there is already a variable with the
     * same reference name.
     */
    public static void defineString(String name) throws ParserException
    {
        define(name, new String());
    }

    /**
     * Defines a variable and asigns a value to it.  The type of variable is
     * inferred from the class of the value passed.
     * @param name The reference name of the variable.
     * @param value The value to assign to the variable.
     * @throws PET.model.ParserException if there is already a variable with the
     * same reference name or if the class of <code>value</code> is not supported.
     */
    public static void define(String name, Object value) throws ParserException
    {
        if(Variables.variables.containsKey(name)) throw new ParserException("Can not redefine variable \""+name+"\".");
        if(!isSupportedVariableClass(value)) throw new ParserException("Variables of type \""+value.getClass()+"\" not supported");

        Variables.variables.put(name, value);

        Variables.getInstance().setChanged();
        Variables.getInstance().notifyObservers(name);
    }

    /**
     * Sets the value of a variable.
     * @param name The reference name of the variable to set.
     * @param value The value to set.
     * @throws PET.model.ParserException if there is already a variable with the
     * same reference name or if the class of <code>value</code> is not the
     * same as the class stored for the variable.
     */
    public static void set(String name, Object value) throws ParserException
    {
        if(!Variables.variables.containsKey(name)) throw new ParserException("Variable \""+name+"\" not defined.");

        Object o = Variables.variables.get(name);
        if(o.getClass() != value.getClass())
            throw new ParserException("Invalid assignment, expecting "+
                    Constants.classNames(o)+", found "+Constants.classNames(value));

        Variables.variables.put(name, value);

        if(PETApp.updateGui) {
            Variables.getInstance().setChanged();
            Variables.getInstance().notifyObservers(name);
        }
    }

    /**
     * Gets the value of the variable.
     * @param name The reference name of the variable to get.
     * @return The value stored in the variable.
     * @throws PET.model.ParserException if there is no variable defined with
     * the given <code>name</code>.
     */
    public static Object get(String name) throws ParserException
    {
        if(! Variables.variables.containsKey(name)) throw new ParserException("Variable \""+name+"\"not defined.");
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
     * Checks to see if we support variables of the given class.
     * @param o An object with the class you want to check.
     * @return <code>true</code> if we support the class.
     */
    private static boolean isSupportedVariableClass(Object o)
    {
        if(o instanceof Boolean) return true;
        if(o instanceof Integer) return true;
        if(o instanceof Integer[]) return true;
        if(o instanceof String) return true;
        
        return false;
    }


    /**
     * sets changed and notifies observers for all variables
     */
    public static void updateAllVariables() {
        
        for(Enumeration e = Variables.variables.keys(); e.hasMoreElements();)
        {
            String name = (String)e.nextElement();

            Variables.getInstance().setChanged();
            Variables.getInstance().notifyObservers(name);

        }
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
