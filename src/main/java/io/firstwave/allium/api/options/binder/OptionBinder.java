package io.firstwave.allium.api.options.binder;

import io.firstwave.allium.api.options.*;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by obartley on 12/1/15.
 */
public abstract class OptionBinder {
    private static final OptionBinder sDefaultBinder = new DefaultBinder();
    private static final Map<Class<? extends Option>, OptionBinder> sBinders = new HashMap<>();

    static {
        registerBinder(Separator.class, new SeparatorBinder());

        registerBinder(BooleanOption.class, new CheckBoxBinder());

        final SliderBinder sb = new SliderBinder();
        registerBinder(IntegerOption.class, sb);
        registerBinder(FloatOption.class, sb);
        registerBinder(DoubleOption.class, sb);

        registerBinder(ColorOption.class, new ColorPickerBinder());
        registerBinder(SingleChoiceOption.class, new ChoiceBoxBinder());
    }

    public static void registerBinder(Class<? extends Option> type, OptionBinder binder) {
        synchronized (sBinders) {
            sBinders.put(type, binder);
        }
    }

    public static void unregisterBinder(Class<? extends Option> type) {
        synchronized (sBinders) {
            sBinders.remove(type);
        }
    }

    public static OptionBinder getBinder(Class<? extends Option> type) {
        synchronized (sBinders) {
            final OptionBinder rv = sBinders.get(type);
            if (rv != null) {
                return rv;
            } else {
                return sBinders.getOrDefault(Option.class, sDefaultBinder);
            }
        }
    }

    public abstract Node bind(final Option option);
    public abstract void update(Node node, final Option option);
}
