package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/7/15.
 */
public class DoubleOption extends Option<Double> {
    public final double min;
    public final double max;

    public DoubleOption(double defaultValue) {
        this(defaultValue, -1.0f, 1.0f);
    }

    public DoubleOption(double defaultValue, double min, double max) {
        super(Double.class, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validate(Object value) {
        return super.validate(value) &&
                (double) value >= min &&
                (double) value <= max;
    }
}
