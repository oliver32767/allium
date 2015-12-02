package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/1/15.
 */
public class IntegerOption extends Option<Integer> {

    public final int min;
    public final int max;

    public IntegerOption(int defaultValue) {
        this(defaultValue, null);
    }

    public IntegerOption(int defaultValue, String description) {
        this(defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, description);
    }

    public IntegerOption(int defaultValue, int min, int max, String description) {
        super(Integer.class, defaultValue, description);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validate(Object value) {
        return super.validate(value) &&
                (int) value >= min &&
                (int) value <= max;
    }
}
