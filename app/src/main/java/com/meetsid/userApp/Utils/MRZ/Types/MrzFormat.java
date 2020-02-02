package com.meetsid.userApp.Utils.MRZ.Types;

import com.meetsid.userApp.Utils.MRZ.MrzParseException;
import com.meetsid.userApp.Utils.MRZ.MrzRange;
import com.meetsid.userApp.Utils.MRZ.MrzRecord;
import com.meetsid.userApp.Utils.MRZ.Records.MRP;

public enum MrzFormat {
    PASSPORT(2, 44, MRP.class);
    public final int rows;
    public final int columns;
    private final Class<? extends MrzRecord> recordClass;

    private MrzFormat(int rows, int columns, Class<? extends MrzRecord> recordClass) {
        this.rows = rows;
        this.columns = columns;
        this.recordClass = recordClass;
    }

    public boolean isFormatOf(String[] mrzRows) {
        return rows == mrzRows.length && columns == mrzRows[0].length();
    }

    public static final MrzFormat get(String mrz) {
        final String[] rows = mrz.split("\n");
        final int cols = rows[0].length();
        for (int i = 1; i < rows.length; i++) {
            if (rows[i].length() != cols) {
                throw new MrzParseException("Different row lengths: 0: " + cols + " and " + i + ": " + rows[i].length(), mrz, new MrzRange(0, 0, 0), null);
            }
        }
        for (final MrzFormat f : values()) {
            if (f.isFormatOf(rows)) {
                return f;
            }
        }
        throw new MrzParseException("Unknown format / unsupported number of cols/rows: " + cols + "/" + rows.length, mrz, new MrzRange(0, 0, 0), null);
    }

    public final MrzRecord newRecord() {
        try {
            return recordClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
