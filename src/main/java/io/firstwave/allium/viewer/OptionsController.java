package io.firstwave.allium.viewer;

import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.Options;
import io.firstwave.allium.api.options.binder.CheckBoxBinder;
import io.firstwave.allium.api.options.binder.OptionBinder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.pmw.tinylog.Logger;

import java.util.List;


/**
 * Created by obartley on 12/2/15.
 */
public class OptionsController {

    public static void registerDefaultBinders() {
        Options.registerBinder(BooleanOption.class, new CheckBoxBinder());
    }

    private final List<Node> mChildList;
    private final SimpleObjectProperty<Options> mOptions = new SimpleObjectProperty<>();

    public OptionsController(Pane pane) {
        mChildList = pane.getChildren();
        mOptions.addListener((observable, oldValue, newValue) -> onOptionsChanged(oldValue, newValue));
    }

    public ObjectProperty<Options> optionsProperty() {
        return mOptions;
    }

    private void onOptionsChanged(Options oldValue, Options newValue) {
        mChildList.clear();

        if (newValue == null) {
            return;
        }

        for (String key : newValue.getKeys()) {
            final Node node = getNodeForOption(key, newValue);
            if (node != null) {
                mChildList.add(node);
            }
        }
    }

    private Node getNodeForOption(String key, Options opts) {
        try {
            final OptionBinder binder = Options.getBinder(opts.getOptionType(key));
            return binder.bind(key, mOptions.getValue());
        } catch (Throwable e) {
            Logger.warn(e);
            return null;
        }
    }


    public void apply() {

    }
}
