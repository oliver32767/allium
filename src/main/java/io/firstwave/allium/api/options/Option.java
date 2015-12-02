package io.firstwave.allium.api.options;

import java.util.Objects;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class Option<T> {

    final T mDefaultValue;
    final Class<T> mType;

    private final String mDescription;

    public Option(Class<T> type, T defaultValue) {
        this(type, defaultValue, null);
    }

    public Option(Class<T> type, T defaultValue, String description) {
        mType = type;
        mDefaultValue = defaultValue;
        mDescription = description;
    }

    public boolean validate(Object value) {
        return (mType.isInstance(value));
    }

    public boolean equals(T oldValue, T newValue) {
        return Objects.equals(oldValue, newValue);
    }
}
