package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/5/15.
 */
public class Separator extends Option<Void> {

    public final String label;

    public Separator() {
        this(null);
    }

    public Separator(String label) {
        super(Void.class, null);
        this.label = label;
    }

    @Override
    public boolean validate(Object value) {
        return value == null;
    }
}
