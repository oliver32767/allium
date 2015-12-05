package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;
import io.firstwave.allium.api.inject.FieldType;
import io.firstwave.allium.api.inject.Inject;
import io.firstwave.allium.api.options.BooleanOption;
import io.firstwave.allium.api.options.FloatOption;
import io.firstwave.allium.api.options.Options;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by obartley on 12/2/15.
 */
public class TroubleMaker extends Layer {

    @Inject(type = FieldType.OPTION)
    private final Layer derpderpderp = null;

    @Inject(key = "derpep")
    private Byte blurp;



    public TroubleMaker() {
        super("trouble");

        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final RuntimeException re = new RuntimeException();
        re.printStackTrace(pw);
        setMessage(sw.toString());

        setOptions(Options.create()
                .add("\n", new BooleanOption(true))
                .add("\t⚙", new FloatOption(0.0f, -Float.MAX_VALUE, Float.MAX_VALUE))
                .build()
        );
    }

    @Override
    protected void onRender(RenderContext ctx) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
}
