package io.firstwave.allium.api.options;

/**
 * Created by obartley on 12/5/15.
 */
public class SingleChoiceOption extends Option<String> {

    public final String[] options;

    public SingleChoiceOption(String defaultValue, String... options) {
        super(String.class, defaultValue);
        this.options = options;
    }

    @Override
    public boolean validate(Object value) {
        boolean found = false;
        if (value != null && value instanceof String) {
            for (String s : options) {
                if (s.equals(value));
                found = true;
                break;
            }
        }
        return found;
    }

    public int indexOfValue() {
        int i = 0;
        for (String s : options) {
            if (s.equals(getString())) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
