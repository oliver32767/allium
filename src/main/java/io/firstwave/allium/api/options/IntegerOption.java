package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/1/15.
 */
public class IntegerOption extends Option<Integer> {

    public final int min;
    public final int max;

    public IntegerOption(int defaultValue) {
        this(defaultValue, 0, 255);
    }

    public IntegerOption(int defaultValue, int min, int max) {
        super(Integer.class, defaultValue);
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
