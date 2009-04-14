/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET;


/**
 *
 * @author crayment
 */
public class Constants
{

    public static final int RUNNING = 0;
    public static final int STOPPED = 1;
    public static final int READY = 2;
    public static final int WAITING_FOR_USER_INPUT = 3;
    public static final int ERROR = -1;

    public static final String RESOURCES_ROOT = "PET/resources/";
    public static final String DEFAULT_FILE_LOCATION = "resources/docs/examples/empty_main.txt";

    /**
     * Defines a mapping of actual java class names to more nub user friendly
     * type names.
     *
     * This class is the same as calling <code>classNames(String c)</code> with
     * <code>object.getClass().toString()</code>.
     * @param o An object of the class you want our friendly name for.
     * @return Our friendly name if we have one, the java class name otherwise.
     */
    public static String classNames(Object o)
    {
        if(o == null) return null;
        if(o.getClass() == null) return null;
        return classNames(o.getClass().toString());
    }

    /**
     * Defines a mapping of actual java class names to more nub user friendly
     * type names.
     *
     * @param c The java class name
     * @return Our user friendly class name.
     */
    public static String classNames(String c)
    {
        if (c.equals("class java.lang.Integer"))
        {
            return "Number";
        } else if (c.equals("class java.lang.Boolean"))
        {
            return "Conditional";
        } else if (c.equals("class [Ljava.lang.Integer;"))
        {
            return "Array";
        } else if (c.equals("class java.lang.String"))
        {
            return "Word";
        } else
        {
            return c;
        }
    }
}
