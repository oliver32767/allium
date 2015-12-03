package io.firstwave.allium.api.options;

import java.util.Objects;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class Option<T> {

    final T mDefaultValue;
    final Class<T> mType;

    public Option(Class<T> type, T defaultValue) {
        mType = type;
        if (!validate(defaultValue)) {
            throw new IllegalArgumentException("Invalid default value for option:" + toString());
        }
        mDefaultValue = defaultValue;
    }

    public boolean validate(Object value) {
        return (mType.isInstance(value));
    }

    public boolean equals(T oldValue, T newValue) {
        return Objects.equals(oldValue, newValue);
    }
}
