/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import PET.model.Variables;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import junit.framework.Assert;

import PET.parser.*;

/**
 *
 * @author crayment
 */
public class EvalTest
{

    public EvalTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void evalClassChecks()
    {
        try
        {
            // Integers
            Assert.assertTrue(Evaluator.eval("5",null).getClass() == Integer.class);
            Assert.assertTrue(Evaluator.eval("-5",null).getClass() == Integer.class);

            Assert.assertTrue(Evaluator.eval("5+5",null).getClass() == Integer.class);
            Assert.assertTrue(Evaluator.eval("(5+5)",null).getClass() == Integer.class);

            // Booleans
            Assert.assertTrue(Evaluator.eval("true",null).getClass() == Boolean.class);
            Assert.assertTrue(Evaluator.eval("false",null).getClass() == Boolean.class);
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalNumberTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("5",null).equals(new Integer(5)));
            Assert.assertTrue(Evaluator.eval("09",null).equals(new Integer(9)));
            Assert.assertTrue(Evaluator.eval("1234567890",null).equals(new Integer(1234567890)));
            Assert.assertTrue(Evaluator.eval("-5",null).equals(new Integer(-5)));
            Assert.assertTrue(Evaluator.eval("-09",null).equals(new Integer(-9)));
            Assert.assertTrue(Evaluator.eval("-1234567890",null).equals(new Integer(-1234567890)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalSimpleAddTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1 + 1",null).equals(new Integer(2)));
            Assert.assertTrue(Evaluator.eval("1+1",null).equals(new Integer(2)));
            Assert.assertTrue(Evaluator.eval("20 + 30",null).equals(new Integer(50)));
            Assert.assertTrue(Evaluator.eval("20+30",null).equals(new Integer(50)));
            Assert.assertTrue(Evaluator.eval("-1 + -1",null).equals(new Integer(-2)));
            Assert.assertTrue(Evaluator.eval("-1+-1",null).equals(new Integer(-2)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalSimpleMultTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1*1",null).equals(new Integer(1)));
            Assert.assertTrue(Evaluator.eval("123456*12345",null).equals(new Integer(1524064320)));
            Assert.assertTrue(Evaluator.eval("-3*2",null).equals(new Integer(-6)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalSimpleDivTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1/1",null).equals(new Integer(1)));
            Assert.assertTrue(Evaluator.eval("123456/12",null).equals(new Integer(10288)));
            Assert.assertTrue(Evaluator.eval("-6/2",null).equals(new Integer(-3)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalSimpleSubTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1-1",null).equals(new Integer(0)));
            Assert.assertTrue(Evaluator.eval("123456-12",null).equals(new Integer(123444)));
            Assert.assertTrue(Evaluator.eval("-3-2",null).equals(new Integer(-5)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalGreaterThan()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1>1",null).equals(new Boolean(1 > 1)));
            Assert.assertTrue(Evaluator.eval("2>1",null).equals(new Boolean(2 > 1)));
            Assert.assertTrue(Evaluator.eval("1>2",null).equals(new Boolean(1 > 2)));
            Assert.assertTrue(Evaluator.eval("1>-3",null).equals(new Boolean(1 > -3)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalLessThan()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1<1",null).equals(new Boolean(1 < 1)));
            Assert.assertTrue(Evaluator.eval("2<1",null).equals(new Boolean(2 < 1)));
            Assert.assertTrue(Evaluator.eval("1<2",null).equals(new Boolean(1 < 2)));
            Assert.assertTrue(Evaluator.eval("1<-3",null).equals(new Boolean(1 < -3)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalLessThanEqual()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1<=1",null).equals(new Boolean(1 <= 1)));
            Assert.assertTrue(Evaluator.eval("2<=1",null).equals(new Boolean(2 <= 1)));
            Assert.assertTrue(Evaluator.eval("1<=2",null).equals(new Boolean(1 <= 2)));
            Assert.assertTrue(Evaluator.eval("1<=-3",null).equals(new Boolean(1 <= -3)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalGreaterThanEqual()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1>=1",null).equals(new Boolean(1 >= 1)));
            Assert.assertTrue(Evaluator.eval("2>=1",null).equals(new Boolean(2 >= 1)));
            Assert.assertTrue(Evaluator.eval("1>=2",null).equals(new Boolean(1 >= 2)));
            Assert.assertTrue(Evaluator.eval("1>=-3",null).equals(new Boolean(1 >= -3)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalPrecedenceTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("3*2+1",null).equals(new Integer(7)));
            Assert.assertTrue(Evaluator.eval("3+2*4",null).equals(new Integer(11)));
            Assert.assertTrue(Evaluator.eval("1+2*3+4",null).equals(new Integer(11)));
            Assert.assertTrue(Evaluator.eval("10-10/2+2",null).equals(new Integer(7)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalAddWithParensTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("1+(1+2)",null).equals((new Integer(4))));
            Assert.assertTrue(Evaluator.eval("(1+(2+3))+4",null).equals((new Integer(10))));
            Assert.assertTrue(Evaluator.eval("1+(2+3)+4",null).equals((new Integer(10))));
            Assert.assertTrue(Evaluator.eval("1+((1+2)+4)",null).equals((new Integer(8))));
            Assert.assertTrue(Evaluator.eval("(1+2)",null).equals((new Integer(3))));
            Assert.assertTrue(Evaluator.eval("(((((((4+(1+2))))))))",null).equals((new Integer(7))));
            Assert.assertTrue(Evaluator.eval("(1+2) + (1+3)",null).equals(new Integer(7)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalMultWithParensTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("(2*2)",null).equals((new Integer(4))));
            Assert.assertTrue(Evaluator.eval("1*(2*3)*4",null).equals((new Integer(24))));
            Assert.assertTrue(Evaluator.eval("(1*2)*3*4",null).equals((new Integer(24))));
            Assert.assertTrue(Evaluator.eval("1*2*(3*4)",null).equals((new Integer(24))));
            Assert.assertTrue(Evaluator.eval("(1*(2*3))*4",null).equals((new Integer(24))));
            Assert.assertTrue(Evaluator.eval("1*((2*3)*4)",null).equals((new Integer(24))));
            Assert.assertTrue(Evaluator.eval("(((((((1*2*3*4)))))))",null).equals((new Integer(24))));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalMixedWithParensTest()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("(1+2)*3*4",null).equals((new Integer(36))));
            Assert.assertTrue(Evaluator.eval("1*(2+3)*4",null).equals((new Integer(20))));
            Assert.assertTrue(Evaluator.eval("1*2*(3+4)",null).equals((new Integer(14))));
            Assert.assertTrue(Evaluator.eval("(1+(2*3))*4",null).equals((new Integer(28))));
            Assert.assertTrue(Evaluator.eval("1*((2+3)+4)",null).equals((new Integer(9))));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalEqual()
    {
        try
        {
            //Integers
            Assert.assertTrue((Boolean) Evaluator.eval("1 == 1",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("(1*1) == (1*1)",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("1*2 == 1+1",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("3*4 == 3 * 4",null) == true);

            //Booleans
            Assert.assertTrue((Boolean) Evaluator.eval("true == true",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("false == false",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("true == false",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("false == true",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("(1==1) == (1==1)",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("true == true == true",null).equals(new Boolean(true == true == true)));
            Assert.assertTrue((Boolean) Evaluator.eval("true == true == true",null).equals(new Boolean((1 * 5) == (10 / 2))));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalNotEq()
    {
        try
        {
            //Integers
            Assert.assertTrue((Boolean) Evaluator.eval("1 not = 1",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("(1*1) not = (1*1)",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("1*2 not = 1+1",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("3*4 not = 3 * 4",null) == false);

            //Booleans
            Assert.assertTrue((Boolean) Evaluator.eval("true not = true",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("false not = false",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("true not = false",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("false not = true",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("(1==1) not = (1==1)",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("true not = true not = true",null).equals(new Boolean(true != true != true)));
            Assert.assertTrue((Boolean) Evaluator.eval("true == true not = true",null).equals(new Boolean(true == true != true)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalNegation()
    {
        try
        {
            Assert.assertTrue((Boolean) Evaluator.eval("not true",null).equals(new Boolean(!true)));
            Assert.assertTrue((Boolean) Evaluator.eval("not false",null).equals(new Boolean(!false)));
            Assert.assertTrue((Boolean) Evaluator.eval("not ((true))",null).equals(new Boolean(!((true)))));
            Assert.assertTrue((Boolean) Evaluator.eval("not (not (false))",null).equals(new Boolean(!(!(false)))));
            Assert.assertTrue((Boolean) Evaluator.eval("not not false",null).equals(new Boolean(! !false)));
            Assert.assertTrue((Boolean) Evaluator.eval("not not not true",null).equals(new Boolean(! ! !true)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalAND()
    {
        try
        {
            Assert.assertTrue((Boolean) Evaluator.eval("true and true",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("false and true",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("true and false",null) == false);
            Assert.assertTrue((Boolean) Evaluator.eval("false and false",null) == false);


            Assert.assertTrue((Boolean) Evaluator.eval("true and true and true",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("false and true and true",null) == false);
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalOR()
    {
        try
        {
            Assert.assertTrue((Boolean) Evaluator.eval("true or true",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("false or true",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("true or false",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("false or false",null) == false);


            Assert.assertTrue((Boolean) Evaluator.eval("false or false or true",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("false or false or false",null) == false);
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalMixedLogic()
    {
        try
        {
            Assert.assertTrue((Boolean) Evaluator.eval("true and true == true",null) == (true && true == true));
            Assert.assertTrue((Boolean) Evaluator.eval("false and false == false",null) == (false && false  == false));
            Assert.assertTrue((Boolean) Evaluator.eval("(1==1) and true == true",null) == ((1==1) && true == true));
            Assert.assertTrue((Boolean) Evaluator.eval("(1==1) and (3==6/2)",null) == ((1==1) && (3==6/2)));
            Assert.assertTrue((Boolean) Evaluator.eval("true or false == true",null) == (true || false == true));
            Assert.assertTrue((Boolean) Evaluator.eval("true or false and true == true",null).equals(new Boolean(true || false && true == true)));
            Assert.assertTrue((Boolean) Evaluator.eval("(not true and not (3==- -5) and false)",null).equals(new Boolean((!true || !(3 == - -5) && false))));
            Assert.assertEquals(Evaluator.eval("(true and (true and true) and ((true and true) and false) and true)",null), new Boolean((true && (true && true) && ((true && true) && false) && true)));
            Assert.assertEquals(Evaluator.eval("(true or (false and true) or ((false and true) and false) and true)",null), new Boolean((true || (false && true) || ((false && true) && false) && true)));
            Assert.assertEquals(Evaluator.eval("(false and (true and true) and ((true and false) and false) and true)",null), new Boolean((false && (true && true) && ((true && false) && false) && true)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalSubVsNeg()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("-2 - -1",null).equals(new Integer(-1)));
            Assert.assertTrue(Evaluator.eval("2 - -1",null).equals(new Integer(3)));
            Assert.assertTrue(Evaluator.eval("-2 - 1",null).equals(new Integer(-3)));
            Assert.assertTrue(Evaluator.eval("-(2 - 1)",null).equals(new Integer(-1)));
            Assert.assertTrue(Evaluator.eval("2+-1",null).equals(new Integer(1)));
            Assert.assertTrue(Evaluator.eval("2+-1-4",null).equals(new Integer(-3)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalDoubleNeg()
    {
        try
        {
            Assert.assertTrue(Evaluator.eval("- -2",null).equals(new Integer(2)));
            Assert.assertTrue(Evaluator.eval("- - - 2",null).equals(new Integer(-2)));
            Assert.assertTrue(Evaluator.eval("- - - - 2",null).equals(new Integer(2)));
            Assert.assertTrue(Evaluator.eval("- -2",null).equals(new Integer(2)));
            Assert.assertTrue(Evaluator.eval("5 + -(-2)",null).equals(new Integer(7)));
            Assert.assertTrue(Evaluator.eval("(4+(- -3-2))",null).equals(new Integer(5)));
            Assert.assertTrue(Evaluator.eval("(5-3)- -(-3)",null).equals(new Integer(-1)));
            Assert.assertTrue(Evaluator.eval("6*- -3",null).equals(new Integer(18)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalVariables()
    {
        try
        {
            Variables.define("first", new Boolean(true));
            Assert.assertTrue(Evaluator.eval("first",null).equals(new Boolean(true)));
            Variables.define("second", new Boolean(false));
            Assert.assertTrue(Evaluator.eval("second",null).equals(new Boolean(false)));
            Assert.assertTrue(Evaluator.eval("first and second",null).equals(new Boolean(false)));

            Variables.define("third", new Integer(5));
            Assert.assertTrue(Evaluator.eval("third",null).equals(new Integer(5)));
            Variables.define("fourth", new Integer(-3));
            Assert.assertTrue(Evaluator.eval("fourth",null).equals(new Integer(-3)));
            Assert.assertTrue(Evaluator.eval("third * fourth",null).equals(new Integer(-15)));

            Variables.define("fifth", "Good");
            Assert.assertTrue(Evaluator.eval("fifth",null).equals("Good"));
            Variables.define("sixth", "Cake");
            Assert.assertTrue(Evaluator.eval("sixth",null).equals("Cake"));

            Variables.clearAllVariables();

        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void stringTest()
    {
        try
        {
            //define variables
            Variables.define("first", "TheCakeIsALie");
            Variables.define("second", "TheCakeIsALie");
            Variables.define("third", "TheCakeIsTrue");

            //check == and !=
            Assert.assertTrue((Boolean) Evaluator.eval("first == second",null) == true);
            Assert.assertFalse((Boolean) Evaluator.eval("first == third",null) == true);
            Assert.assertFalse((Boolean) Evaluator.eval("first not = second",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("first not = third",null) == true);

            Variables.clearAllVariables();
        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void evalArrayAccess()
    {
        try
        {
            Variables.define("first", new Integer[]
                    {
                        1, 2, 3, 4, 5
                    });
            Assert.assertEquals(Evaluator.eval("first[0]",null), 1);
            for (int i = 0; i < 5; i++)
            {
                Assert.assertTrue(Evaluator.eval("first[" + i + "]",null).equals(new Integer(i + 1)));
            }


            Variables.clearAllVariables();
        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
    }



    @Test
    public void evalComplexArrayAccess()
    {
        try
        {
            Variables.define("first", new Integer[]
                    {
                        1, 2, 3, 4, 5
                    });


            Assert.assertEquals(Evaluator.eval("first[0] + first[first[0]+1]", true,null), 4);


            Variables.clearAllVariables();
        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
    }

    /**
     * Must evaluate things in order.  "i>0 and array[i]" should not error when i < 0
     * and "i == -1 or array[i] == -1" should not error for i = -1
     */
    @Test
    public void variablesEvaluationTest()
    {
        try
        {
            //define variables
            Variables.define("array", new Integer[1]);
            Variables.define("i", -1);

            //check == and !=
            Assert.assertTrue((Boolean) Evaluator.eval("(i > 0) and (array[i] > 0)",null) == false);

            Assert.assertTrue((Boolean) Evaluator.eval("i == -1 or array[i] == -1",null) == true);

 
            Variables.clearAllVariables();
        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
        int i;
    }



    /**
     * Try to break the Evaluator with complex operations here.
     */
    @Test
    public void evalComplex()
    {
        try
        {
            Assert.assertTrue((Boolean) Evaluator.eval("(1+(2*3))+(5*(3)) == 11 * 4 / 2",null) == true);
            Assert.assertTrue((Boolean) Evaluator.eval("-(2+-3-7)/(8*2-12)==2",null) == true);
            Assert.assertTrue(Evaluator.eval("5 - (- - 5 + 4)",null).equals(new Integer(-4)));
            Assert.assertTrue(Evaluator.eval("- -(6*- -3) - -5 * 7 - 4",null).equals(new Integer(- -(6 * - -3) - -5 * 7 - 4)));
        }
        catch (ParserException e)
        {
            Assert.fail(e.toString());
        }
    }
}