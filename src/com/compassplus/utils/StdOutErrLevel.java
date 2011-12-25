package com.compassplus.utils;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 25.12.11
 * Time: 22:44
 */
public class StdOutErrLevel extends Level {

    private StdOutErrLevel(String name, int value) {
        super(name, value);
    }

    public static Level STDOUT = new StdOutErrLevel("STDOUT", Level.INFO.intValue() + 53);

    public static Level STDERR = new StdOutErrLevel("STDERR", Level.INFO.intValue() + 54);

    protected Object readResolve()
            throws ObjectStreamException {
        if (this.intValue() == STDOUT.intValue())
            return STDOUT;
        if (this.intValue() == STDERR.intValue())
            return STDERR;
        throw new InvalidObjectException("Unknown instance :" + this);
    }
}
