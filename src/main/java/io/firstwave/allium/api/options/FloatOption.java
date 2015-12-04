package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/4/15.
 */
public class FloatOption extends Option<Float> {

    public final float min;
    public final float max;

    public FloatOption(float defaultValue) {
        this(defaultValue, -1.0f, 1.0f);
    }

    public FloatOption(float defaultValue, float min, float max) {
        super(Float.class, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validate(Object value) {
        return super.validate(value) &&
                (float) value >= min &&
                (float) value <= max;
    }
}
