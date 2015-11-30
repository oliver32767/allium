package io.firstwave.allium.core;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by obartley on 11/28/15.
 */
public class ConfigurationTest {

    private Configuration mConfig;

    @Before
    public void setUp() {
        mConfig = new Configuration.Builder()
                .addOptionItem("opt", true)
                .addOptionSetItem("opt_set", 1, "foo", "bar")
                .addIntegerItem("int", 1, Integer.MIN_VALUE, Integer.MAX_VALUE)
                .addFloatItem("float", 1, -Float.MAX_VALUE, Float.MAX_VALUE)
                .addStringItem("str", "string")
                .build();
    }


    @Test
    public void testKeySet() throws Exception {
        final Set<String> keySet = mConfig.keySet();
        assertEquals(5, keySet.size());
        assertTrue(keySet.contains("opt"));
        assertTrue(keySet.contains("opt_set"));
        assertTrue(keySet.contains("int"));
        assertTrue(keySet.contains("float"));
        assertTrue(keySet.contains("str"));
    }

    @Test
    public void testGetType() throws Exception {
        assertTrue(mConfig.getType("opt") == Configuration.Type.OPTION);
        assertTrue(mConfig.getType("int") == Configuration.Type.INTEGER);
        assertTrue(mConfig.getType("float") == Configuration.Type.FLOAT);
    }

    @Test
    public void testGetOption() throws Exception {
        assertTrue(mConfig.getOption("opt"));
    }

    @Test
    public void testGetOptionSetIndex() throws Exception {
        assertEquals(1, mConfig.getOptionSetIndex("opt_set"));
    }

    @Test
    public void testGetOptionSet() throws Exception {
        final String[] set = mConfig.getOptionSet("opt_set");
        assertEquals(2, set.length);
        assertEquals("foo", set[0]);
        assertEquals("bar", set[1]);
    }

    @Test
    public void testGetInteger() throws Exception {
        assertEquals(1, mConfig.getInteger("int"));
    }

    @Test
    public void testGetIntegerRange() throws Exception {
        assertEquals(Integer.MIN_VALUE, mConfig.getIntegerRange("int").min);
        assertEquals(Integer.MAX_VALUE, mConfig.getIntegerRange("int").max);
    }

    @Test
    public void testGetFloat() throws Exception {
        assertEquals(1f, mConfig.getFloat("float"), 0.001f);
    }

    @Test
    public void testGetFloatRange() throws Exception {
        assertEquals(-Float.MAX_VALUE, mConfig.getFloatRange("float").min, 0.001f);
        assertEquals(Float.MAX_VALUE, mConfig.getFloatRange("float").max, 0.001f);
    }

    @Test
    public void testEdit() throws Exception {
        Configuration.Editor ed = mConfig.edit()
                .setOption("opt", false)
                .setOptionSetIndex("opt_set", 0)
                .setInteger("int", 12)
                .setFloat("float", 420)
                .setString("str", "test");

        assertEquals(1, mConfig.getInteger("int"));
        assertEquals(1f, mConfig.getFloat("float"), 0.001f);

        ed.commit();

        assertFalse(mConfig.getOption("opt"));
        assertEquals(0, mConfig.getOptionSetIndex("opt_set"));
        assertEquals(12, mConfig.getInteger("int"));
        assertEquals(420, mConfig.getFloat("float"), 0.001f);
        assertEquals("test", mConfig.getString("str"));
    }
}