package io.firstwave.allium.api.options;

import java.util.Objects;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class Option<T> {

    final T mDefaultValue;
    final Class<T> mValueType;

    Options mOptions;
    String mKey;
    String mDescription;

    public Option(Class<T> valueType, T defaultValue) {
        mValueType = valueType;
        mDefaultValue = defaultValue;
    }

    public final Class<T> getValueType() {
        return mValueType;
    }

    public final byte getByte() {
        return (byte) get();
    }

    public final short getShort() {
        return (short) get();
    }

    public final int getInt() {
        return (int) get();
    }

    public final long getLong() {
        return (long) get();
    }

    public final float getFloat() {
        return (float) get();
    }

    public final double getDouble() {
        return (double) get();
    }

    public final boolean getBoolean() {
        return (boolean) get();
    }

    public final char getChar() {
        return (char) get();
    }

    public final String getString() {
        return (String) get();
    }

    @SuppressWarnings("unchecked")
    public final Object get() {
        if (mOptions != null) {
            return mOptions.get(mKey);
        }
        return null;
    }

    public final Editor getEditor() {
        return new Editor(this);
    }


    public final String getKey() {
        return mKey;
    }

    public final String getDescription() {
        return mDescription;
    }

    public boolean validate(Object value) {
        return value == null || mValueType.isAssignableFrom(value.getClass());
    }

    public boolean equals(T oldValue, T newValue) {
        return Objects.equals(oldValue, newValue);
    }

    public static final class Editor {
        private final Option mOption;

        private Editor(Option option) {
            mOption = option;
        }

        public void setByte(byte value) {
            set(value);
        }

        public void setShort(short value) {
            set(value);
        }

        public void setInt(int value) {
            set(value);
        }

        public void setLong(long value) {
            set(value);
        }

        public void setFloat(float value) {
            set(value);
        }

        public void setDouble(double value) {
            set(value);
        }

        public void setBoolean(boolean value) {
            set(value);
        }

        public void setChar(char value) {
            set(value);
        }

        public void setString(String value) {
            set(value);
        }


        public void set(Object value) {
            if (mOption.mOptions != null) {
                mOption.mOptions.getEditor().set(mOption.mKey, value);
            }
        }

        public void cancel() {
            if (mOption.mOptions != null) {
                mOption.mOptions.getEditor().cancel(mOption.mKey);
            }
        }
    }
}
