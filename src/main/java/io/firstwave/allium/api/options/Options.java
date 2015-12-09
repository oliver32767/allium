package io.firstwave.allium.api.options;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.pmw.tinylog.Logger;

import java.util.*;

/**
 * Created by obartley on 12/1/15.
 */
public final class Options {

    public static final Options EMPTY = new Options();
    private static final Editor READ_ONLY_EDITOR = new Editor(null) {
        @Override
        public Editor set(String key, Object value) {
            throw new UnsupportedOperationException();
        }
    };

    public static Options unmodifiableCopy(Options options) {
        final Options rv = new Options(options);
        rv.mEditor = READ_ONLY_EDITOR;
        return rv;
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder buildUpon(Options options) {
        if (options == null) {
            return new Builder();
        }
        return new Builder(options);
    }

    private final BooleanProperty mUnmodified = new SimpleBooleanProperty(false);

    private final Map<String, Option> mItems;
    private final Map<String, Object> mValues;

    private Editor mEditor;

    private Options() {
        mItems = new HashMap<>();
        mValues = new HashMap<>();
    }

    private Options(Options copy) {
        if (copy == null) {
            copy = EMPTY;
        }
        mItems = copy.mItems;
        mValues = copy.mValues;
    }

    private Options(Builder b) {
        mItems = new LinkedHashMap<>(b.mItems);
        mValues = new HashMap<>(b.mValues);
    }

    public byte getByte(String key) {
        return (byte) get(key);
    }

    public short getShort(String key) {
        return (short) get(key);
    }

    public int getInt(String key) {
        return (int) get(key);
    }

    public long getLong(String key) {
        return (long) get(key);
    }

    public float getFloat(String key) {
        return (float) get(key);
    }

    public double getDouble(String key) {
        return (double) get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) get(key);
    }

    public char getChar(String key) {
        return (char) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public Object get(String key) {
        return mValues.get(key);
    }

    public Option<?> getOption(String key) {
        final Option<?> rv = mItems.get(key);
        if (rv == null) {
            return null;
        }
        rv.mOptions = this;
        return rv;
    }

    public Class<? extends Option> getOptionType(String key) {
        final Option opt = mItems.get(key);
        if (opt == null) {
            return null;
        }
        return opt.getClass();
    }

    public Set<String> getKeys() {
        return mItems.keySet();
    }

    public Editor getEditor() {
        if (mEditor == null) {
            mEditor = new Editor(this);
        }
        return mEditor;
    }

    public Builder buildUpon() {
        return buildUpon(this);
    }

    public BooleanProperty unmodifiedProperty() {
        return mUnmodified;
    }

    private int apply(Editor editor) {
        // this has all been validated
        if (editor.mChanges.size() == 0) {
            Logger.debug("No changes applied");
            return 0;
        }
        mValues.putAll(editor.mChanges);

        final int rv = editor.mChanges.size();

        editor.mChanges.clear();
        mUnmodified.setValue(true);
        Logger.debug("Applied " + rv + " change(s)");
        return rv;
    }

    public static class Editor {
        private final Options mOptions;
        private Editor(Options options) {
            mOptions = options;
        }

        private Map<String, Object> mChanges = new HashMap<>();

        public void setByte(String key, byte value) {
            set(key, value);
        }

        public void setShort(String key, short value) {
            set(key, value);
        }

        public void setInt(String key, int value) {
            set(key, value);
        }

        public void setLong(String key, long value) {
            set(key, value);
        }

        public void setFloat(String key, float value) {
            set(key, value);
        }

        public void setDouble(String key, double value) {
            set(key, value);
        }

        public void setBoolean(String key, boolean value) {
            set(key, value);
        }

        public void setChar(String key, char value) {
            set(key, value);
        }

        public void setString(String key, String value) {
            set(key, value);
        }

        @SuppressWarnings("unchecked")
        public Editor set(String key, Object value) {
            final Option opt = mOptions.getOption(key);

            if (opt == null) {
                Logger.warn("Key " + key + " is not mapped to an option");
                return this;
            }

            final Class<?> type = opt.mValueType;
            if (!mOptions.validateType(type, key)) {
                return this;
            }

            if (!opt.validate(value)) {
                Logger.warn("Value <" + value + "> is invalid for key " + key);
                return this;
            }

            if (!mOptions.mValues.containsKey(key) || !opt.equals(mOptions.mValues.get(key), value)) {
                Logger.debug("added " + key + " -> " + value + " to list of changes");
                mChanges.put(key, value);
            } else {
                Logger.debug("removed: " + key + " -> " + value + " from list of changes");
                mChanges.remove(key);
            }


            mOptions.mUnmodified.setValue(!mChanges.isEmpty());
            return this;
        }

        public int apply() {
            return mOptions.apply(this);
        }

        public void cancel() {
            mChanges.clear();
            mOptions.mUnmodified.setValue(true);
        }

        void cancel(String key) {
            mChanges.remove(key);
            mOptions.mUnmodified.setValue(!mChanges.isEmpty());
        }
    }

    public static final class Builder {

        private final Map<String, Option> mItems;
        private final Map<String, Object> mValues;

        public Builder() {
            mItems = new LinkedHashMap<>();
            mValues  = new HashMap<>();
        }

        public Builder(Options copy) {
            mItems = new LinkedHashMap<>(copy.mItems);
            mValues = new HashMap<>(copy.mValues);
            if (mItems.size() > 0) {
                addSeparator();
            }
        }

        public Builder add(String key, Option item) {
            return add(key, null, item);
        }

        public Builder add(String key, String description, Option item) {
            if (key == null || item == null) {
                throw new NullPointerException();
            }
            if (!item.validate(item.mDefaultValue)) {
                throw new IllegalArgumentException("Invalid default value");
            }
            mItems.put(key, item);
            mValues.put(key, item.mDefaultValue);

            item.mKey = key;
            item.mDescription = description;

            return this;
        }

        public Builder addSeparator() {
            return addSeparator(null);
        }

        public Builder addSeparator(String label) {
            add(UUID.randomUUID().toString(), new Separator(label));
            return this;
        }



        public Options build() {
            return new Options(this);
        }
    }

    private boolean validateType(Class<?> type, String key) {
        final Option opt = getOption(key);
        if (opt == null) {
            typeWarning(type, key);
            return false;
        }
        final boolean rv = opt.mValueType.equals(type);
        if (!rv) {
            typeWarning(type, key);
        }
        return rv;
    }

    private void typeWarning(Class<?> type, String key) {
        Logger.warn("Key " + key + " does not match type " + type);
    }
}
