package io.firstwave.allium.api.inject;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.Scene;
import io.firstwave.allium.api.options.Options;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by obartley on 12/3/15.
 */
public class Injector {

    public static void inject(Layer layer) {
        if (layer == null) {
            throw new NullPointerException();
        }
        new Injector(layer, null, layer).inject(); // TODO: fix options support
    }

    public static void inject(Scene scene) {
        if (scene == null) {
            throw new NullPointerException();
        }
        new Injector(scene, null, scene.getRoot()).inject(); // TODO: fix options support
    }

    private final Object mTarget;
    private final Options mOptions;
    private final Layer mLayer;

    private Injector(Object target, Options options, Layer layer) {
        mTarget = target;
        mOptions = options;
        mLayer = layer;
    }

    private void inject() {
        final Map<Field, Inject> annos = new LinkedHashMap<>();
        for (Field field : mTarget.getClass().getDeclaredFields()) {
            final Inject anno = field.getAnnotation(Inject.class);
            if (anno != null) {
                annos.put(field, anno);
            }
        }
        for (Field field : annos.keySet()) {
            inject(field, annos.get(field));
        }
    }

    private void inject(Field field, Inject anno) {
        Logger.trace("Injecting " + field.toGenericString() + " using " + anno.key() + ":" + anno.type());
        // TODO
    }


}
