package com.jkqj.opensearch.sdk.jdbc.ext;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OsJdbcConfig {
    private boolean autoCommit;
    private long connectionTimeout;
    private long idleTimeout;
    private long maxLifetime;
    private int minimumIdle;
    private int maximumPoolSize;
    private String password;
    private String username;
    private String url;
}
