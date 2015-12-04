package io.firstwave.allium.viewer;

import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.api.options.binder.CheckBoxBinder;
import io.firstwave.allium.api.options.binder.OptionBinder;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.pmw.tinylog.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by obartley on 12/2/15.
 */
public class OptionsController {

    public static void registerDefaultBinders() {
        Options.registerBinder(BooleanOption.class, new CheckBoxBinder());
    }

    private final List<Node> mChildList;
    private final SimpleObjectProperty<Options> mOptions = new SimpleObjectProperty<>();
    private final Map<String, Node> mNodeMap = new HashMap<>();

    public OptionsController(Pane pane) {
        mChildList = pane.getChildren();
        mOptions.addListener((observable, oldValue, newValue) -> onOptionsChanged(oldValue, newValue));
    }

    public Options getOptions() {
        return mOptions.get();
    }

    public void setOptions(Options options) {
        this.mOptions.set(options);
    }

    private void onOptionsChanged(Options oldValue, Options newValue) {
        mChildList.clear();
        mNodeMap.clear();

        if (newValue == null) {
            return;
        }

        for (String key : newValue.getKeys()) {
            final Node node = getNodeForOption(key, newValue);
            if (node != null) {
                mNodeMap.put(key, node);
                mChildList.add(node);
            }
        }
        reset();
    }

    private Node getNodeForOption(String key, Options opts) {
        try {
            final OptionBinder binder = getBinderForOption(key, opts);
            return binder.bind(key, mOptions.getValue());
        } catch (Throwable e) {
            Logger.warn(e);
            return null;
        }
    }

    private OptionBinder getBinderForOption(String key, Options opts) {
        return Options.getBinder(opts.getOptionType(key));
    }

    public void reset() {
        final Options opts = mOptions.getValue();

        if (opts != null) {
            opts.edit().cancel();
        } else {
            return;
        }

        for (String key : mNodeMap.keySet()) {
            reset(key, mNodeMap.get(key), opts);
        }
    }

    private void reset(String key, Node node, Options opts) {
        if (node != null && opts != null) {
            final OptionBinder b = getBinderForOption(key, opts);
            b.updateValue(node, key, opts);
        }
    }

    public int apply() {
        final Options opts = mOptions.getValue();
        if (opts == null) {
            return 0;
        }
        return opts.edit().apply();

    }
}
