package io.firstwave.allium.core;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import org.pmw.tinylog.Logger;

import java.util.*;

/**
 * Created by obartley on 11/27/15.
 */
public final class Configuration {

    public enum Type {
        OPTION,
        OPTION_SET,
        INTEGER, FLOAT,
        STRING,
    }

    public static final Configuration EMPTY = new Configuration();

    private final Map<String, Item> mItems;
    private final Map<String, Object> mValues;
    private final Map<String, String> mDescriptions;

    private final Set<OnConfigurationChangedListener> mConfigurationChangedListeners = new HashSet<OnConfigurationChangedListener>();

    private final SimpleBooleanProperty mUnchanged = new SimpleBooleanProperty(true);

    private Editor mEditor;

    private Configuration() {
        mItems = new HashMap<String, Item>();
        mValues = new HashMap<String, Object>();
        mDescriptions = new HashMap<String, String>();
    }

    private Configuration(Builder b) {
        mItems = new LinkedHashMap<String, Item>(b.mItems);
        mValues = new HashMap<String, Object>(b.mValues);
        mDescriptions = new HashMap<String, String>(b.mDescriptions);
    }

    public boolean addOnConfigurationChangedListener(OnConfigurationChangedListener listener) {
        return mConfigurationChangedListeners.add(listener);
    }

    public boolean removeOnConfigurationChangedListener(OnConfigurationChangedListener listener) {
        return mConfigurationChangedListeners.remove(listener);
    }

    private void dispatchConfigurationChanged() {
        for (OnConfigurationChangedListener l : mConfigurationChangedListeners) {
            l.onConfigurationChanged(this);
        }
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

    public Object getValue(String key) {
        if (mValues.containsKey(key)) {
            return mValues.get(key);
        }
        return null;
    }

    public String getDescription(String key) {
        return mDescriptions.getOrDefault(key, null);
    }
    
    public boolean getOption(String key) {
        if (getType(key) != Type.OPTION) {
            throw new IllegalArgumentException(key + " is not an option type!");
        }
        return (Boolean) mValues.get(key);
    }

    public int getOptionSetIndex(String key) {
        if (getType(key) != Type.OPTION_SET) {
            throw new IllegalArgumentException(key + " is not an option set type!");
        }
        return (Integer) mValues.get(key);
    }

    public String[] getOptionSet(String key) {
        if (getType(key) != Type.OPTION_SET) {
            throw new IllegalArgumentException(key + " is not an option set type!");
        }
        return (String[]) mItems.get(key).params;
    }
    
    public int getInteger(String key) {
        if (getType(key) != Type.INTEGER) {
            throw new IllegalArgumentException(key + " is not an integer type!");
        }
        return (Integer) mValues.get(key);
    }
    
    public IntegerRange getIntegerRange(String key) {
        if (getType(key) != Type.INTEGER) {
            throw new IllegalArgumentException(key + " is not an integer type");
        }
        return (IntegerRange) mItems.get(key).params;
    }

    public float getFloat(String key) {
        if (getType(key) != Type.FLOAT) {
            throw new IllegalArgumentException(key + " is not a float type!");
        }
        return (Float) mValues.get(key);
    }

    public FloatRange getFloatRange(String key) {
        if (getType(key) != Type.FLOAT) {
            throw new IllegalArgumentException(key + " is not a float type");
        }
        return (FloatRange) mItems.get(key).params;
    }

    public String getString(String key) {
        if (getType(key) != Type.STRING) {
            throw new IllegalArgumentException(key + " is not a string type!");
        }
        return (String) mValues.get(key);
    }

    private void apply(Editor editor) {
        cancel(editor);
        final Map<String, Object> changes = new HashMap<String, Object>(editor.mChanges);
        for (String key : changes.keySet()) {
            Logger.debug("applying " + key + "->" + changes.get(key));
            mValues.put(key, changes.get(key));
        }
        dispatchConfigurationChanged();
    }

    private void cancel(Editor editor) {
        if (mEditor == null || mEditor != editor) {
            throw new IllegalStateException("Invalid editor!");
        }
        mEditor = null;
        mUnchanged.setValue(true);
    }

    public Editor edit() {
        if (mEditor == null) {
            mEditor = new Editor();
        }
        return mEditor;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Configuration.class.getSimpleName() + "[");
        String prefix = "";
        for (String key : keySet()) {
            sb.append(prefix);
            prefix = ", ";
            sb.append(key);
            sb.append(":");
            sb.append(mValues.get(key));
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean isEditing() {
        return mEditor != null;
    }

    public ObservableBooleanValue unchangedProperty() {
        return mUnchanged;
    }


    public final class Editor {
        private final Map<String, Object> mChanges = new HashMap<String, Object>();


        private Editor() {}

        public Editor setOption(String key, boolean value) {
            putValue(key, value);
            return this;
        }

        public Editor setOptionSetIndex(String key, int index) {
            putValue(key, index);
            return this;
        }

        public Editor setInteger(String key, int value) {
            putValue(key, value);
            return this;
        }

        public Editor setFloat(String key, float value) {
            putValue(key, value);
            return this;
        }

        public Editor setString(String key, String value) {
            putValue(key, value);
            return this;
        }

        private void putValue(String key, Object value) {
            if (!mItems.containsKey(key)) {
                throw new IllegalArgumentException("Key not found:" + key);
            }
            mItems.get(key).validate(value);
            if (compare(key, value)) {
                mChanges.remove(key);
            } else {
                mChanges.put(key, value);
            }
            mUnchanged.setValue(mChanges.isEmpty());
            Logger.trace(this);
        }

        private boolean compare(String key, Object value) {
            final Object curr = mValues.get(key);

            if (value == null || curr == null) {
                return value == curr;
            }

            if (value.getClass().isArray()) {
                return Arrays.equals((Object[]) value, (Object[]) curr);
            } else {
                return value.equals(curr);
            }
        }

        public void cancel() {
            Configuration.this.cancel(this);
        }

        public int apply() {
            Configuration.this.apply(this);
            return mChanges.size();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(Configuration.class.getSimpleName() + "[");
            String prefix = "";
            for (String key : mChanges.keySet()) {
                sb.append(prefix);
                prefix = ", ";
                sb.append(key);
                sb.append(":");
                sb.append(mValues.get(key));
                sb.append("->");
                sb.append(mChanges.get(key));
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static final class Builder {
        private Map<String, Item> mItems = new LinkedHashMap<String, Item>();
        private Map<String, Object> mValues = new HashMap<String, Object>();
        private Map<String, String> mDescriptions = new HashMap<String, String>();

        public Builder setDescription(String key, String description) {
            mDescriptions.put(key, description);
            return this;
        }

        public Builder addOptionItem(String key, boolean defaultValue) {
            final Item item = new Item(Type.OPTION, null);

            mItems.put(key, item);
            mValues.put(key, defaultValue);

            return this;
        }

        public Builder addOptionSetItem(String key, int defaultValue, String... options) {
            final Item item = new Item(Type.OPTION_SET, options);

            item.validate(defaultValue);
            mItems.put(key, item);
            mValues.put(key, defaultValue);

            return this;
        }

        public Builder addIntegerItem(String key, int defaultValue) {
            return addIntegerItem(key, defaultValue, 0, 255);
        }

        public Builder addIntegerItem(String key, int defaultValue, int min, int max) {
            final IntegerRange params = new IntegerRange(min, max);
            final Item item = new Item(Type.INTEGER, params);

            item.validate(defaultValue);
            mItems.put(key, item);
            mValues.put(key, defaultValue);

            return this;
        }

        public Builder addFloatItem(String key, float defaultValue) {
            return addFloatItem(key, defaultValue, -1.0f, 1.0f);
        }

        public Builder addFloatItem(String key, float defaultValue, float min, float max) {
            final FloatRange params = new FloatRange(min, max);
            final Item item = new Item(Type.FLOAT, params);

            item.validate(defaultValue);
            mItems.put(key, item);
            mValues.put(key, defaultValue);

            return this;
        }

        public Builder addStringItem(String key, String defaultValue) {
            final Item item = new Item(Type.STRING, null);

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
        private final Object params;

        private Item(Type type, Object params) {
            this.type = type;
            this.params = params;
        }

        private void validate(Object value) {
            boolean valid = true;
            try {
                switch (type) {
                    case OPTION:
                        final Boolean b = (Boolean) value;
                        break;
                    case OPTION_SET:
                        final Integer idx = (Integer) value;
                        final String[] opts = (String[]) params;
                        valid = (idx != null) &&
                                (idx >= 0) &&
                                (idx < opts.length);
                        break;
                    case INTEGER:
                        final Integer i = (Integer) value;
                        final IntegerRange ir = (IntegerRange) params;
                        valid = (ir.min <= i && i <= ir.max);
                        break;
                    case FLOAT:
                        final Float f = (Float) value;
                        final FloatRange fr = (FloatRange) params;
                        valid = (fr.min <= f && f <= fr.max);
                        break;
                    case STRING:
                        final String s2 = (String) value;
                        valid = true;
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

    public static class IntegerRange {
        public final int min;
        public final int max;

        private IntegerRange(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    public static class FloatRange {
        public final float min;
        public final float max;

        private FloatRange(float min, float max) {
            this.min = min;
            this.max = max;
        }
    }

    public interface OnConfigurationChangedListener {
        void onConfigurationChanged(Configuration config);
    }
}
