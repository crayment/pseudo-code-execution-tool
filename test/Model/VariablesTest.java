/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import PET.parser.ParserException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import PET.model.*;
import junit.framework.Assert;
/**
 *
 * @author crayment
 */
public class VariablesTest {

    public VariablesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        Variables.clearAllVariables();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void simpleDefineAndGetIntegerTest()
    {
        try{
            Integer first = new Integer(12);
            Variables.defineNumber("first");
            Assert.assertTrue(Variables.isDefined("first"));
            Variables.set("first", first);
            Object retrieved = Variables.get("first");
            Assert.assertTrue(retrieved.getClass() == Integer.class);
            Integer newD = Integer.parseInt(retrieved.toString());
            Assert.assertEquals(first, newD);
        }
        catch(ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void simpleDefineAndGetBooleanTest()
    {
        try{
            Boolean first = new Boolean(false);
            Variables.defineCond("first");
            Assert.assertTrue(Variables.isDefined("first"));
            Variables.set("first", first);
            Object retrieved = Variables.get("first");
            Assert.assertTrue(retrieved.getClass() == Boolean.class);
            Boolean newD = Boolean.parseBoolean(retrieved.toString());
            Assert.assertEquals(first, newD);
        }
        catch(ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void intsTest()
    {
        try{
            //define some ints
            Variables.define("first", new Integer(12));
            Variables.define("second", new Integer(0));
            Variables.define("third", new Integer(-8));
            Variables.define("fourth", new Integer(50));
            //were they defined properly
            Assert.assertEquals(Variables.get("first"), new Integer(12));
            Assert.assertEquals(Variables.get("second"), new Integer(0));
            Assert.assertEquals(Variables.get("third"), new Integer(-8));
            Assert.assertEquals(Variables.get("fourth"), new Integer(50));
            //change some
            Variables.set("second", new Integer(10));
            Variables.set("third", new Integer(-11));
            //were the correct ones changed
            Assert.assertEquals(Variables.get("first"), new Integer(12));
            Assert.assertEquals(Variables.get("second"), new Integer(10));
            Assert.assertEquals(Variables.get("third"), new Integer(-11));
            Assert.assertEquals(Variables.get("fourth"), new Integer(50));

        }
        catch(ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void booleansTest()
    {
        try{
            //define some bools
            Variables.define("first", new Boolean(true));
            Variables.define("second", new Boolean(true));
            Variables.define("third", new Boolean(false));
            Variables.define("fourth", new Boolean(false));
            //were they defined properly
            Assert.assertEquals(Variables.get("first"), new Boolean(true));
            Assert.assertEquals(Variables.get("second"), new Boolean(true));
            Assert.assertEquals(Variables.get("third"), new Boolean(false));
            Assert.assertEquals(Variables.get("fourth"), new Boolean(false));
            //change some
            Variables.set("second", new Boolean(false));
            Variables.set("third", new Boolean(true));
            //were the correct ones changed
            Assert.assertEquals(Variables.get("first"),new Boolean(true));
            Assert.assertEquals(Variables.get("second"), new Boolean(false));
            Assert.assertEquals(Variables.get("third"), new Boolean(true));
            Assert.assertEquals(Variables.get("fourth"), new Boolean(false));

        }
        catch(ParserException e)
        {
            Assert.fail(e.toString());
        }
    }
    
    @Test
    public void arrayTest()
    {
        try{
            
            //define base objects
            Integer[] array1 = new Integer[1];
            Integer[] array2 = new Integer[10];
            Integer[] array3 = new Integer[10];
            Integer[] array4 = new Integer[100000];
            Integer[] target;
            
            // define some arrays
            Variables.define("array1", array1);
            Variables.define("array2", array2);
            Variables.define("array3", array3);
            Variables.define("array4", array4);

            //check definitions
            Assert.assertEquals(Variables.get("array1"), array1);
            Assert.assertEquals(Variables.get("array2"), array2);
            Assert.assertEquals(Variables.get("array3"), array3);
            Assert.assertEquals(Variables.get("array4"), array4);
            
            //grab first array
            target = (Integer[]) Variables.get("array4");
            
            //assign values for first array, changing value number 3
            target[0] = new Integer(1);
            target[1] = new Integer(100);
            target[2] = new Integer(0);
            target[3] = new Integer(10000000);
            target[4] = new Integer(-1000000);
            target[3] = new Integer(50);
            
            //clear and then regrab array
            target = null;
            target = (Integer[]) Variables.get("array4");
            
            //check values
            Assert.assertEquals(target[0], new Integer(1));
            Assert.assertEquals(target[1], new Integer(100));
            Assert.assertEquals(target[2], new Integer(0));
            Assert.assertEquals(target[3], new Integer(50));
            Assert.assertEquals(target[4], new Integer(-1000000));
            
        }
         catch(Exception e)
        {
            Assert.fail(e.toString());
        }
    }
    
    
    @Test
    public void testString(){
        try{
            String temp;
            
            //Create Variables
            Variables.define("String1", "Matricies");
            Variables.define("String2", "On");
            Variables.define("String3", "A");
            Variables.define("String4", "Plane");
            
            //Check definition
            Assert.assertTrue(Variables.get("String1").equals("Matricies"));
            Assert.assertTrue(Variables.get("String2").equals("On"));
            Assert.assertTrue(Variables.get("String3").equals("A"));
            Assert.assertTrue(Variables.get("String4").equals("Plane"));
            
            //Change Strings 1 and 4
            Variables.set("String1", "Snakes");
            Variables.set("String4", "Train");
            
            //Re-check variables.
            Assert.assertFalse(Variables.get("String1").equals("Matricies"));
            Assert.assertTrue(Variables.get("String1").equals("Snakes"));
            Assert.assertTrue(Variables.get("String2").equals("On"));
            Assert.assertTrue(Variables.get("String3").equals("A"));
            Assert.assertFalse(Variables.get("String4").equals("Plane"));
            Assert.assertTrue(Variables.get("String4").equals("Train"));
            
        }
        catch(Exception e)
        {
            Assert.fail(e.toString());
        }
    }
}