package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/1/15.
 */
public class BooleanOption extends Option<Boolean> {

    public BooleanOption(boolean defaultValue) {
        super(Boolean.class, defaultValue);
    }

    public BooleanOption(boolean defaultValue, String description) {
        super(Boolean.class, defaultValue, description);
    }

}
