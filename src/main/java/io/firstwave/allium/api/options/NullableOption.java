package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/2/15.
 */
public class NullableOption<T> extends Option<T> {
    public NullableOption(Class<T> type, T defaultValue) {
        super(type, defaultValue);
    }

    @Override
    public boolean validate(Object value) {
        return value == null || super.validate(value);
    }
}
