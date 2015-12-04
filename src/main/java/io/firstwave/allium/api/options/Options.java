package io.firstwave.allium.api.options;


import io.firstwave.allium.api.options.binder.DefaultBinder;
import io.firstwave.allium.api.options.binder.OptionBinder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.pmw.tinylog.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by obartley on 12/1/15.
 */
public final class Options {

    // STATIC BINDER API ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final OptionBinder sDefaultBinder = new DefaultBinder();
    private static final Map<Class<? extends Option>, OptionBinder> sBinders = new HashMap<>();

    public static void registerBinder(Class<? extends Option> type, OptionBinder binder) {
        synchronized (sBinders) {
            sBinders.put(type, binder);
        }
    }

    public static void unregisterBinder(Class<? extends Option> type) {
        synchronized (sBinders) {
            sBinders.remove(type);
        }
    }

    public static OptionBinder getBinder(Class<? extends Option> type) {
        synchronized (sBinders) {
            final OptionBinder rv = sBinders.get(type);
            if (rv != null) {
                return rv;
            } else {
                return sBinders.getOrDefault(Option.class, sDefaultBinder);
            }
        }
    }

    public static final Options EMPTY = new Options();

    public static Options unmodifiableOptions(Options options) {
        return new Options(options);
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
        mEditor = new Editor() {
            @Override
            public int apply() {
                throw new UnsupportedOperationException();
            }
        };

    }

    private Options(Builder b) {
        mItems = new LinkedHashMap<>(b.mItems);
        mValues = new HashMap<>(b.mValues);
    }

    public Object getValue(String key) {
        return mValues.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> type, String key) {
        if (!validateType(type, key)) {
            return null;
        }
        return (T) getValue(key);
    }

    public Class<?> getValueType(String key) {
        if (!mItems.containsKey(key)) {
            return null;
        }
        return mItems.get(key).getType();
    }

    public Option<?> getOption(String key) {
        return mItems.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Option<T> getOption(Class<T> type, String key) {
        if (!validateType(type, key)) {
            return null;
        }
        return (Option<T>) getOption(key);
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

    public Editor edit() {
        if (mEditor == null) {
            mEditor = new Editor();
        }
        return mEditor;
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

    public class Editor {

        private Editor() {
        }

        private Map<String, Object> mChanges = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <T> Editor set(Class<T> type, String key, T value) {

            if (!validateType(type, key)) {
                return this;
            }

            final Option opt = mItems.get(key);

            if (opt == null) {
                Logger.warn("Key " + key + " is not mapped to an option");
                return this;
            } else if (!opt.mType.equals(type)) {
                typeWarning(type, key);
                return this;
            }

            if (!opt.validate(value)) {
                Logger.warn("Value <" + value + "> is invalid for key " + key);
                return this;
            }

            if (!mValues.containsKey(key) || !opt.equals(mValues.get(key), value)) {
                Logger.debug("added " + key + " -> " + value + " to list of changes");
                mChanges.put(key, value);
            } else {
                Logger.debug("removed: " + key + " -> " + value + " from list of changes");
                mChanges.remove(key);
            }


            mUnmodified.setValue(!mChanges.isEmpty());
            return this;
        }

        public int apply() {
            return Options.this.apply(this);
        }

        public void cancel() {
            mChanges.clear();
            mUnmodified.setValue(true);
        }
    }

    public static final class Builder {
        private final Map<String, Option> mItems = new LinkedHashMap<>();
        private final Map<String, Object> mValues = new HashMap<>();

        public Builder add(String key, Option item) {
            if (key == null || item == null) {
                throw new NullPointerException();
            }
            if (!item.validate(item.mDefaultValue)) {
                throw new IllegalArgumentException("Invalid default value");
            }
            mItems.put(key, item);
            mValues.put(key, item.mDefaultValue);
            return this;
        }

        public Options build() {
            return new Options(this);
        }
    }

    private boolean validateType(Class<?> type, String key) {
        final Option opt = mItems.get(key);
        if (opt == null) {
            typeWarning(type, key);
            return false;
        }
        final boolean rv = opt.mType.equals(type);
        if (!rv) {
            typeWarning(type, key);
        }
        return rv;
    }

    private void typeWarning(Class<?> type, String key) {
        Logger.warn("Key " + key + " does not match type " + type);
    }
}
