package io.firstwave.allium.viewer;

import io.firstwave.allium.core.Configuration;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.pmw.tinylog.Logger;

import java.util.List;

/**
 * Created by obartley on 11/29/15.
 */
public class ConfigurationController {
    private final List<Node> mChildList;

    private final SimpleObjectProperty<Configuration> mConfiguration = new SimpleObjectProperty<Configuration>(null);
    private final ChangeListener<Configuration> mConfigurationChangeListener = new ChangeListener<Configuration>() {
        @Override
        public void changed(ObservableValue<? extends Configuration> observable, Configuration oldValue, Configuration newValue) {
            onConfigurationChanged(oldValue, newValue);
        }
    };
    private final Configuration.OnConfigurationChangedListener mOnConfigurationChangedListener = new Configuration.OnConfigurationChangedListener() {
        @Override
        public void onConfigurationChanged(Configuration config) {
            Logger.debug("configuration changed:" + config);
        }
    };

    public ConfigurationController(Pane pane) {
        mChildList = pane.getChildren();
        mConfiguration.addListener(mConfigurationChangeListener);
    }

    public SimpleObjectProperty<Configuration> configurationProperty() {
        return mConfiguration;
    }

    public void cancel() {
        onConfigurationChanged(mConfiguration.getValue(), mConfiguration.getValue());
    }

    public int apply() {
        int rv = 0;
        if (mConfiguration.getValue() != null ) {
            if (mConfiguration.getValue().isEditing()) {
                rv = mConfiguration.getValue().edit().apply();
            }
        }
        return rv;
    }

    private void onConfigurationChanged(Configuration oldValue, Configuration newValue) {
        mChildList.clear();

        if (oldValue != null && oldValue.isEditing()) {
            oldValue.removeOnConfigurationChangedListener(mOnConfigurationChangedListener);
            oldValue.edit().cancel();
        }

        if (newValue == null) {
            return;
        }
        newValue.addOnConfigurationChangedListener(mOnConfigurationChangedListener);

        for (String key : newValue.keySet()) {
            final Node node = getNodeForConfiguration(key, newValue);
            if (node != null) {
                final String desc = newValue.getDescription(key);
                if (desc != null && !desc.trim().equals("")) {
                    Tooltip.install(node, new Tooltip(desc));
                }
                mChildList.add(node);
            }
        }
    }

    private Node getNodeForConfiguration(String key, Configuration configuration) {
        final Configuration.Type type = configuration.getType(key);
        if (type == null) {
            return null;
        }
        switch (type) {
            case OPTION:
                return bindOptionType(key, configuration);
            case OPTION_SET:
                return bindOptionSetType(key, configuration);
            case INTEGER:
                return bindInteger(key, configuration);
            case FLOAT:
                return bindFloat(key, configuration);
            case STRING:
                return bindString(key, configuration);
            default:
                return null;
        }
    }

    private Node bindOptionType(final String key, final Configuration configuration) {
        CheckBox rv = new CheckBox(key);


        rv.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                configuration.edit().setOption(key, newValue);
                Logger.debug(configuration.edit());
            }
        });
        rv.selectedProperty().setValue(configuration.getOption(key));

        return rv;
    }

    private Node bindOptionSetType(final String key, final Configuration configuration) {

        final VBox rv = new VBox();
        rv.getChildren().add(new Label(key));

        final ToggleGroup tg = new ToggleGroup();
        final String[] options = configuration.getOptionSet(key);
        final int index = configuration.getOptionSetIndex(key);
        for (int i = 0; i < options.length; i++) {
            final RadioButton rb = new RadioButton(options[i]);
            rb.setUserData(i);
            rb.setToggleGroup(tg);
            if (i == index) {
                rb.setSelected(true);
            }
            rv.getChildren().add(rb);
        }

        tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (tg.getSelectedToggle() != null) {
                    configuration.edit().setOptionSetIndex(key, (Integer) newValue.getUserData());
                }
            }
        });
        return rv;
    }

    private Node bindInteger(final String key, final Configuration configuration) {

        final VBox rv = new VBox();
        final Label lbl = new Label(key + ":" + String.valueOf(configuration.getInteger(key)));
        rv.getChildren().add(lbl);

        final Configuration.IntegerRange range = configuration.getIntegerRange(key);
        final Slider sl = new Slider(range.min, range.max, configuration.getInteger(key));
        sl.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                configuration.edit().setInteger(key, (int) sl.getValue());
                lbl.setText(String.valueOf(key + ":" + (int) sl.getValue()));
            }
        });

        rv.getChildren().add(sl);

        return rv;
    }

    private Node bindFloat(final String key, final Configuration configuration) {

        final VBox rv = new VBox();
        final Label lbl = new Label(key + ":" + String.valueOf(configuration.getFloat(key)));
        rv.getChildren().add(lbl);

        final Configuration.FloatRange range = configuration.getFloatRange(key);
        final Slider sl = new Slider(range.min, range.max, configuration.getFloat(key));
        sl.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                configuration.edit().setFloat(key, (float) sl.getValue());
                lbl.setText(String.valueOf(key + ":" + (float) sl.getValue()));
            }
        });
        sl.setBlockIncrement(0.1);
        rv.getChildren().add(sl);

        return rv;
    }

    private Node bindString(final String key, final Configuration configuration) {
        final VBox rv = new VBox(new Label(key));

        final TextField tf = new TextField(configuration.getString(key));
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                configuration.edit().setString(key, newValue);
            }
        });

        rv.getChildren().add(tf);
        return rv;
    }
}
