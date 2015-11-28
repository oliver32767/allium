package io.firstwave.allium.core;

/**
 * Created by obartley on 11/27/15.
 */
public class Layer implements Configurable {
    private final Configuration mConfig;

    public Layer() {
        this(Configuration.EMPTY);
    }

    public Layer(Configuration config) {
        mConfig = config;
    }

    @Override
    public final Configuration getConfiguration() {
        return mConfig;
    }
}
