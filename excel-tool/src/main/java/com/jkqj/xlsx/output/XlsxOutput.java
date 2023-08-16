package com.jkqj.xlsx.output;

import java.io.OutputStream;
import java.util.List;

public interface XlsxOutput {
    <T> void output(List<T> dataList, OutputStream outputStream);
}
