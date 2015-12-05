package io.firstwave.allium.viewer;

import io.firstwave.allium.api.options.Option;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.api.options.binder.OptionBinder;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.pmw.tinylog.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by obartley on 12/2/15.
 */
public class OptionsController {

    private final List<Node> mChildList;
    private final SimpleObjectProperty<Options> mOptions = new SimpleObjectProperty<>();
    private final Map<Option, Node> mNodeMap = new HashMap<>();

    public OptionsController(Pane pane) {
        mChildList = pane.getChildren();
        mOptions.addListener((observable, oldValue, newValue) -> onOptionsChanged(oldValue, newValue));
        onOptionsChanged(null, null);
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

        if (newValue == null || newValue.getKeys().size() == 0) {
            final Label lbl = new Label("No options available for current selection");
            lbl.setWrapText(true);
            lbl.setOpacity(0.5);
            mChildList.add(lbl);
            return;
        }

        for (String key : newValue.getKeys()) {
            final Option option = newValue.getOption(key);

            if (option != null) {
                Node node;
                try {
                    node = getNodeForOption(option);
                    if (node == null) {
                        node = OptionBinder.getBinder(null).bind(option);
                    }
                } catch (Throwable tr) {
                    Logger.warn(tr);
                    node = new Label("Error binding: " + key);
                }
                if (node != null) {
                    mNodeMap.put(option, node);
                    mChildList.add(node);
                }
            }
        }
        reset();
    }

    private Node getNodeForOption(Option option) {
        try {
            final OptionBinder binder = getBinderForOption(option);
            final Node rv = binder.bind(option);
            if (rv == null) {
                Logger.warn("Binder " + binder + " could not bind option: " + option.getKey());
            }
            return rv;
        } catch (Throwable e) {
            Logger.warn(e);
            return null;
        }
    }

    private OptionBinder getBinderForOption(Option option) {
        return OptionBinder.getBinder(option.getClass());
    }

    public void reset() {
        final Options opts = mOptions.getValue();

        if (opts != null) {
            opts.getEditor().cancel();
        } else {
            return;
        }

        for (Option option : mNodeMap.keySet()) {
            reset(mNodeMap.get(option), option);
        }
    }

    private void reset(Node node, Option option) {
        if (node != null && option != null) {
            final OptionBinder b = getBinderForOption(option);
            try {
                b.update(node, option);
            } catch (Throwable tr) {
                Logger.warn(tr);
            }
        }
    }

    public int apply() {
        final Options opts = mOptions.getValue();
        if (opts == null) {
            return 0;
        }
        return opts.getEditor().apply();
    }
}
