/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import PET.Constants;

/**
 *
 * @author crayment
 */
public class ConstantsTest {

    public ConstantsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void classNamesTest()
    {
        Assert.assertEquals(Constants.classNames(Integer.class.toString()), "Number");
        Assert.assertEquals(Constants.classNames(Boolean.class.toString()), "Conditional");
        Assert.assertEquals(Constants.classNames(new Boolean(true)), "Conditional");
        Assert.assertEquals(Constants.classNames(new Integer(0)), "Number");
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

}