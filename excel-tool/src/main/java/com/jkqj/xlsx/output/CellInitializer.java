package com.jkqj.xlsx.output;

import com.aspose.cells.License;

class CellInitializer {
    private static final CellInitializer INSTANCE = new CellInitializer();

    static {
        License.fuck();
    }

    private CellInitializer() {

    }

    public static void init() {

    }
}
