package io.firstwave.allium.api.options;

import javafx.scene.paint.Color;

/**
 * Created by obartley on 12/5/15.
 */
public class ColorOption extends Option<Color> {
    public ColorOption(Color defaultValue) {
        super(Color.class, defaultValue);
    }

    public ColorOption(Color defaultValue, String description) {
        super(Color.class, defaultValue, description);
    }
}
