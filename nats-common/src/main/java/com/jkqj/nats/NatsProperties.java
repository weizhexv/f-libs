package com.jkqj.nats;

import io.nats.client.Options;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "mq.nats")
@Data
public class NatsProperties {

    /**
     * URL for the nats server, can be a comma separated list.
     */
    private String[] servers;

    /**
     * Connection name, shows up in thread names.
     */
    private String connectionName;

    /**
     * Maximum reconnect attempts if a connection is lost, after the initial connection.
     */
    private int maxReconnect = Options.DEFAULT_MAX_RECONNECT;

    /**
     * Time to wait between reconnect attempts to the same server url.
     */
    private Duration reconnectWait = Options.DEFAULT_RECONNECT_WAIT;

    /**
     * Timeout for the initial connection, if this time is passed, the connection will fail
     * and no reconnect attempts are made.
     */
    private Duration connectionTimeout = Options.DEFAULT_CONNECTION_TIMEOUT;

    /**
     * Time between pings to the server to check "liveness".
     */
    private Duration pingInterval = Options.DEFAULT_PING_INTERVAL;

    /**
     * Size of the buffer, in bytes, used to hold outgoing messages during reconnect.
     */
    private long reconnectBufferSize = Options.DEFAULT_RECONNECT_BUF_SIZE;

    /**
     * Prefix to use for inboxes, generally the default is used but custom prefixes
     * can allow security controls.
     */
    private String inboxPrefix = Options.DEFAULT_INBOX_PREFIX;

    /**
     * Whether or not the server will send messages sent from this connection back to the connection.
     */
    private boolean noEcho;

    /**
     * Authentication user name. Requires the password, but not the token, or credentials, or NKey.
     */
    private String username;

    /**
     * Authentication password. Requires the username, but not the token, or credentials, or NKey.
     */
    private String password;

    /**
     * checkpoint configuration.
     */
    private CheckpointProperties checkpoint;

    /**
     * The number of pulls that can be outstanding on a pull consumer
     */
    private Long maxPullWaiting = 512L;

    /**
     * Connection pool size
     */
    private int poolSize = 5;

    /**
     * Stream cluster replicas
     */
    private int replicas = 1;

    public NatsProperties() {
    }

    @Data
    public static class CheckpointProperties {
        /**
         * checkpoint appId. different service identity.
         */
        private String appId;

        /**
         * whether enable checkpoint
         */
        private boolean enable;
    }
}
