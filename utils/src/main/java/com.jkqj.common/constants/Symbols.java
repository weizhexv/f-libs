package com.jkqj.common.constants;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * 符号常量
 *
 * @author cb
 * @date 2020-09-29
 */
public interface Symbols {

    String NEW_LINE = "\n";

    String UNDERLINE = "_";

    String HYPHEN = "-";

    String VERTICAL = "|";

    String DOUBLE_VERTICAL = "||";

    String COMMA = ",";

    String SEMICOLON = ";";

    String COLON = ":";

    String POINT = ".";

    String TAB = "\t";

    String BLANK = " ";

    Splitter LINE_SPLITTER = Splitter.on(NEW_LINE).omitEmptyStrings().trimResults();

    Splitter SPLITTER = Splitter.on(COMMA).omitEmptyStrings().trimResults();

    Splitter UNDERLINE_SPLITTER = Splitter.on(UNDERLINE).omitEmptyStrings().trimResults();

    Splitter HYPHEN_SPLITTER = Splitter.on(HYPHEN).omitEmptyStrings().trimResults();

    Splitter VERTICAL_SPLITTER = Splitter.on(VERTICAL).omitEmptyStrings().trimResults();

    Splitter DOUBLE_VERTICAL_SPLITTER = Splitter.on(DOUBLE_VERTICAL).omitEmptyStrings().trimResults();

    Splitter SEMI_SPLITTER = Splitter.on(SEMICOLON).omitEmptyStrings().trimResults();

    Splitter COLON_SPLITTER = Splitter.on(COLON).omitEmptyStrings().trimResults();

    Splitter TAB_SPLITTER = Splitter.on(TAB).omitEmptyStrings().trimResults();

    Splitter BLANK_SPLITTER = Splitter.on(BLANK).omitEmptyStrings().trimResults();

    Joiner LINE_JOINER = Joiner.on(NEW_LINE).skipNulls();

    Joiner JOINER = Joiner.on(COMMA).skipNulls();

    Joiner UNDERLINE_JOINER = Joiner.on(UNDERLINE).skipNulls();

    Joiner HYPHEN_JOINER = Joiner.on(HYPHEN).skipNulls();

    Joiner VERTICAL_JOINER = Joiner.on(VERTICAL).skipNulls();

    Joiner DOUBLE_VERTICAL_JOINER = Joiner.on(DOUBLE_VERTICAL).skipNulls();

    Joiner SEMI_JOINER = Joiner.on(SEMICOLON).skipNulls();

    Joiner COLON_JOINER = Joiner.on(COLON).skipNulls();

    Joiner TAB_JOINER = Joiner.on(TAB).skipNulls();

    Joiner BLANK_JOINER = Joiner.on(BLANK).skipNulls();

}