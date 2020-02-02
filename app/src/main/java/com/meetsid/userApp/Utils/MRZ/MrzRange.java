package com.meetsid.userApp.Utils.MRZ;

import java.io.Serializable;

public class MrzRange implements Serializable {
    private static final long serialVersionUID = 1L;
    public final int column;
    public final int columnTo;
    public final int row;

    public MrzRange(int column, int columnTo, int row) {
        if (column > columnTo) {
            throw new IllegalArgumentException("Parameter column: invalid value " + column + ": must be less than " + columnTo);
        }
        this.column = column;
        this.columnTo = columnTo;
        this.row = row;
    }

    @Override
    public String toString() {
        return "" + column + "-" + columnTo + "," + row;
    }

    public int length() {
        return columnTo - column;
    }
}
