package com.meetsid.userApp.Utils.MRZ;

import com.meetsid.userApp.Utils.MRZ.Types.MrzFormat;

public class MrzParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public final String mrz;
    public final MrzRange range;
    public final MrzFormat format;

    public MrzParseException(String message, String mrz, MrzRange range, MrzFormat format) {
        super("Failed to parse MRZ " + format + " " + mrz + " at " + range + ": " + message);
        this.mrz = mrz;
        this.format = format;
        this.range = range;
    }
}
