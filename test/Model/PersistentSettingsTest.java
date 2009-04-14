/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import PET.model.*;
import java.util.Hashtable;
import junit.framework.Assert;

/**
 *
 * @author Abhinav
 */
public class PersistentSettingsTest
{

    public PersistentSettingsTest()
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

    /**
     * Some basic tests.
     */
    @Test
    public void defaultsTest()
    {
        try
        {
            PersistentSettings ps = new PersistentSettings();
            SettingsMgr sm = SettingsMgr.getInstance();
            sm.writeObject(ps);
            Assert.assertEquals(ps.getFontSize(), 14);

            ps.setFontType("TestType");
            ps.setFontSize(13);
            sm.writeObject(ps);
            PersistentSettings ps2 = (PersistentSettings) sm.readObject(ps.getClass().getName());
            Assert.assertEquals(ps2.getFontSize(), 13);
            Assert.assertEquals(ps2.getFontType(), "TestType");

            sm.removeSettings(ps2);

        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
    }

    /**
     * Tests to make sure that the written object is same as retrived object.
     */
    @Test
    public void equalTest()
    {
        try
        {
            PersistentSettings ps1 = new PersistentSettings();
            PersistentSettings ps2 = new PersistentSettings();
            SettingsMgr sm = SettingsMgr.getInstance();

            sm.writeObject(ps1);
            ps2 = (PersistentSettings) sm.readObject(ps2.getClass().getName());

            Assert.assertTrue(ps1.equals(ps2));
            sm.removeSettings(ps1);

        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
    }

    /**
     * Tests to make sure that the written object is same as retrived object.
     */
    @Test
    public void recentfilesTest()
    {
        try
        {
            RecentFiles rf = new RecentFiles();
            SettingsMgr sm = SettingsMgr.getInstance();
            rf.addFile("test1");
            sm.writeObject(rf);

            RecentFiles rf2 = new RecentFiles();
            rf2 = (RecentFiles) sm.readObject(rf2.getClass().getName());
            Assert.assertTrue(rf2.getFiles().contains("test1"));

        }
        catch (Exception e)
        {
            Assert.fail(e.toString());
        }
    }
}
