package com.jkqj.nats;

import java.util.Optional;

public enum TestSubjects implements Subject {
    TEST_USER_CREATED("test", "user.created"),
    TEST_USER_CREATED_DELAY("test", "user.created.delay"),
    TEST_USER_CREATED_PULL("test", "user.created.pull"),
    TEST_USER_CREATED_PULL_FROM("test", "user.created.pullfrom"),
    TEST_USER_CREATED_PULL_LB("test", "user.created.pulllb"),
    TEST_USER_CREATED_OFFSET("test", "user.created.offset"),
    TEST_USER_CREATED_GROUP("test", "user.created.group", "groupABC"),
    TEST_USER_UPDATED("test", "user.updated"),
    TEST_USER_UPDATED_OFFSET("test", "user.updated.offset");

    private final String category;
    private final String name;
    private String group;

    TestSubjects(String category, String name) {
        this.category = category;
        this.name = name;
    }

    TestSubjects(String category, String name, String group) {
        this.category = category;
        this.name = name;
        this.group = group;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> getGroup() {
        return Optional.ofNullable(group);
    }
}
