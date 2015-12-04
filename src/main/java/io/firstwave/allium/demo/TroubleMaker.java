package io.firstwave.allium.demo;

import io.firstwave.allium.api.Layer;
import io.firstwave.allium.api.RenderContext;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by obartley on 12/2/15.
 */
public class TroubleMaker extends Layer {

    public TroubleMaker() {
        super("trouble");

        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        final RuntimeException re = new RuntimeException();
        re.printStackTrace(pw);
        setMessage(sw.toString());
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
