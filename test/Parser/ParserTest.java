/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import PET.parser.*;
import PET.model.*;


import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author crayment
 */
public class ParserTest
{

    public ParserTest()
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
        Variables.clearAllVariables();
        Parser.reset();
    }




}
