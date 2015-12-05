package io.firstwave.allium.api.inject;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.options.Options;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by obartley on 12/3/15.
 */
public class Injector {

    public static void inject(Object target, Layer source) {
        new Injector(target, source.getOptions(), source).inject();
    }

    private final Object mTarget;
    private final Options mOptions;
    private final Layer mLayer;

    private Injector(Object target, Options options, Layer layer) {
        Logger.debug("Injecting " + target + " using: " + options + ", " + layer);
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
        try {
            if (field != null) {
                Logger.trace("Injecting " + field.toGenericString() + " using " +
                        ("".equals(anno.key()) ? "<empty>" : anno.key()) +
                        ":" + anno.type());
                field.setAccessible(true);
            } else {
                return;
            }
            switch (anno.type()) {
                case AUTO:
                    if (Layer.class.isAssignableFrom(field.getType())) {
                        injectLayer(field, anno);
                    } else {
                        injectOption(field, anno);
                    }
                    break;
                case LAYER:
                    injectLayer(field, anno);
                    break;
                case OPTION:
                    injectOption(field, anno);
            }
        } catch (IllegalAccessException | ClassCastException | IllegalArgumentException e) {
            Logger.warn("Couldn't inject field:" + field.getName() + " " + e.getMessage());
        } catch (NullPointerException ignored) {

        }

    }

    private void injectLayer(Field field, Inject anno) throws IllegalAccessException {
        if ("".equals(anno.key())) {
            field.set(mTarget, mLayer.findChildByName(field.getName()));
        } else {
            field.set(mTarget, mLayer.findChildByName(anno.key()));
        }
        Logger.trace("Injected layer " + field.getName() + " -> " + field.get(mTarget));
    }

    private void injectOption(Field field, Inject anno) throws IllegalAccessException {
        if ("".equals(anno.key())) {
            field.set(mTarget, mOptions.get(field.getName()));
        } else {
            field.set(mTarget, mOptions.get(anno.key()));
        }
        Logger.trace("Injected option " + field.getName() + " -> " + field.get(mTarget));
    }
}
