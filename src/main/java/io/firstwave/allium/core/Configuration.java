package io.firstwave.allium.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by obartley on 11/27/15.
 */
public final class Configuration {

    public enum Type {
        OPTION,
        INTEGER, FLOAT,
        SINGLE_CHOICE,
        MULTI_CHOICE
    }

    public static final Configuration EMPTY = new Configuration();

    public static final int MIN = 0;
    public static final int MAX = 1;

    private final Map<String, Item> mItems;
    private final Map<String, Object> mValues;

    private Editor mEditor;

    private Configuration() {
        mItems = new HashMap<String, Item>();
        mValues = new HashMap<String, Object>();
    }

    private Configuration(Builder b) {
        mItems = new HashMap<String, Item>(b.mItems);
        mValues = new HashMap<String, Object>(b.mValues);
    }
    
    public Set<String> keySet() {
        return mItems.keySet();
    }
    
    public Type getType(String key) {
        if (mItems.containsKey(key)) {
            return mItems.get(key).type;
        }
        return null;
    }
    
    public boolean getOption(String key) {
        if (getType(key) != Type.OPTION) {
            throw new IllegalArgumentException(key + " is not an option type!");
        }
        return (Boolean) mValues.get(key);
    }
    
    public int getInteger(String key) {
        if (getType(key) != Type.INTEGER) {
            throw new IllegalArgumentException(key + " is not an integer type!");
        }
        return (Integer) mValues.get(key);
    }
    
    public int[] getIntegerRange(String key) {
        if (getType(key) != Type.INTEGER) {
            throw new IllegalArgumentException(key + " is not an integer type");
        }
        return (int[]) mItems.get(key).params; 
    }

    public float getFloat(String key) {
        if (getType(key) != Type.FLOAT) {
            throw new IllegalArgumentException(key + " is not a float type!");
        }
        return (Float) mValues.get(key);
    }

    public float[] getFloatRange(String key) {
        if (getType(key) != Type.FLOAT) {
            throw new IllegalArgumentException(key + " is not a float type");
        }
        return (float[]) mItems.get(key).params;
    }

    public Editor edit() {
        if (mEditor == null) {
            mEditor = new Editor();
        }
        return mEditor;
    }

    public final class Editor {
        private final Map<String, Object> mChanges = new HashMap<String, Object>();

        private Editor() {
        }

        public boolean isChanged() {
            return !mChanges.isEmpty();
        }

        public void setOption(String key, boolean value) {
            putValue(key, value);
        }

        public void setInteger(String key, int value) {
            putValue(key, value);
        }



        private void putValue(String key, Object value) {
            mItems.get(key).validate(value);
            if (compare(key, value)) {
                mChanges.remove(key);
            } else {
                mChanges.put(key, value);
            }
        }

        private boolean compare(String key, Object value) {
            final Object curr = mValues.get(key);

            if (value == null || curr == null) {
                return value == curr;
            }

            if (!value.getClass().isArray()) {
                return value.equals(curr);
            }

            for (Object v : (Object[]) value) {
                for (Object c : (Object[]) curr) {
                    if (!v.equals(c)) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void commit() {

        }
    }

    public static final class Builder {
        private Map<String, Item> mItems = new HashMap<String, Item>();
        private Map<String, Object> mValues = new HashMap<String, Object>();


        public Builder addOptionItem(String key, String label, boolean defaultValue) {
            final Item item = new Item(Type.OPTION, label, null);

            mItems.put(key, item);
            mValues.put(key, defaultValue);
            return this;
        }

        public Builder addIntegerItem(String key, String label, int defaultValue, int min, int max) {
            final int[] params = new int[2];
            params[MIN] = min;
            params[MAX] = max;
            final Item item = new Item(Type.INTEGER, label, params);

            item.validate(defaultValue);
            mItems.put(key, item);
            mValues.put(key, defaultValue);

            return this;
        }

        public Builder addFloatItem(String key, String label, float defaultValue, float min, float max) {
            final float[] params = new float[2];
            params[MIN] = min;
            params[MAX] = max;
            final Item item = new Item(Type.FLOAT, label, params);

            item.validate(defaultValue);
            mItems.put(key, item);
            mValues.put(key, defaultValue);

            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }

    private static class Item {
        private final Type type;
        private final String label;
        private final Object params;

        private Item(Type type, String label, Object params) {
            this.type = type;
            this.label = label;
            this.params = params;
        }

        private void validate(Object value) {
            boolean valid = true;
            try {
                switch (type) {
                    case OPTION:
                        final Boolean b = (Boolean) value;
                        break;
                    case INTEGER:
                        final Integer i = (Integer) value;
                        final Integer[] is = (Integer[]) params;
                        valid = (is[0] <= i && i <= is[1]);
                        break;
                    case FLOAT:
                        final Float f = (Float) value;
                        final Float[] fs = (Float[]) params;
                        valid = (fs[0] <= f && f <= fs[1]);
                        break;
                    case SINGLE_CHOICE:
                        final Integer i2 = (Integer) value;
                        final String[] ss = (String[]) params;
                        valid = (0 <= i2 && i2 < ss.length);
                        break;
                    case MULTI_CHOICE:
                        final Integer[] is2 = (Integer[]) value;
                        final String[] ss2 = (String[]) params;
                        for (Integer i4 : is2) {
                            valid = valid && (0 <= i4 && i4 < ss2.length);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown item type");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Wrong value type");
            }

            if (!valid) {
                throw new IllegalArgumentException("Value out of bounds!");
            }
        }
    }
}
