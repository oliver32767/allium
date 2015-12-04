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
        assertTrue(mOptions.get(Boolean.class, "boolean"));
        assertEquals(7, (int) mOptions.get(Integer.class, "int"));
    }

    @Test
    public void testGetOption() throws Exception {
        assertEquals(Boolean.class, mOptions.getOption(Boolean.class, "boolean").mType);
        assertEquals(Integer.class, mOptions.getOption(Integer.class, "int").mType);
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
        assertEquals(Boolean.class, mOptions.getOption("boolean").getType());
        assertEquals(Integer.class, mOptions.getOption("int").getType());
    }

    @Test
    public void testEdit() throws Exception {
        mOptions.edit().set(Boolean.class, "boolean", false);
        mOptions.edit().set(Integer.class, "int", 4);
        mOptions.edit().apply();

        assertFalse(mOptions.get(Boolean.class, "boolean"));
        assertEquals(4, (int) mOptions.get(Integer.class, "int"));

    }
}