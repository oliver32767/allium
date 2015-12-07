package io.firstwave.allium.api;

/**
 * Created by obartley on 12/2/15.
 */
public enum RenderState {
    IDLE,

    PREPARING,
    READY,

    RENDERING,
    PUBLISHED,
    ERROR
}