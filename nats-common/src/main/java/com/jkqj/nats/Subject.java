package com.jkqj.nats;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public interface Subject {

    String ILLEGAL_REGEX = "[.|*>]";
    String DASH = "-";

    String getCategory();

    String getName();

    default Optional<String> getGroup() {
        return Optional.empty();
    }

    default String getId() {
        return StringUtils.join(getCategory(), getName(), getGroup().orElse(""))
                .replaceAll(ILLEGAL_REGEX, DASH);
    }

    static void check(Subject that) {
        checkArgument(StringUtils.isNotBlank(that.getCategory()));
        checkArgument(StringUtils.isNotBlank(that.getName()));
    }
}
