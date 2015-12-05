package io.firstwave.allium.api.options;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by obartley on 12/2/15.
 */
public class OptionsTest {

    private Options mOptions;

    @Before
    public void setUp() throws Exception {
        mOptions = new Options.Builder()
                .add("boolean", new BooleanOption(true))
                .add("int", new IntegerOption(7))
                .build();
    }

    @Test
    public void testGet() throws Exception {
        assertTrue(mOptions.getBoolean("boolean"));
        assertEquals(7, mOptions.getInt("int"));
    }

    @Test
    public void testGetOption() throws Exception {
        assertEquals(Boolean.class, mOptions.getOption("boolean").getValueType());
        assertEquals(Integer.class, mOptions.getOption("int").getValueType());
    }

    @Test
    public void testGetKeys() throws Exception {
        final Set<String> keys = mOptions.getKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("boolean"));
        assertTrue(keys.contains("int"));
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(Boolean.class, mOptions.getOption("boolean").getValueType());
        assertEquals(Integer.class, mOptions.getOption("int").getValueType());
    }

    @Test
    public void testEdit() throws Exception {
        mOptions.getEditor().setBoolean("boolean", false);
        mOptions.getEditor().setInt("int", 4);
        mOptions.getEditor().apply();

        assertFalse(mOptions.getBoolean("boolean"));
        assertEquals(4, mOptions.getInt("int"));

    }
}