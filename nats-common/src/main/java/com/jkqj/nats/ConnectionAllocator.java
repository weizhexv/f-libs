package com.jkqj.nats;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ConnectionAllocator {
    private final NatsProperties properties;

    public ConnectionAllocator(NatsProperties properties) {
        this.properties = properties;
    }

    public synchronized Connection allocate(ConnectionListener connectionListener) {
        log.debug("initializing connection with {}", properties);
        var options = buildOptions(properties, connectionListener);
        log.debug("build options {}", options);

        try {
            return Nats.connect(options);
        } catch (IOException | InterruptedException e) {
            log.error("can't create nats connection", e);
            throw new RuntimeException(e);
        }
    }

    private Options buildOptions(NatsProperties properties, ConnectionListener connectionListener) {
        var builder = new Options.Builder()
                .connectionName(properties.getConnectionName())
                .servers(properties.getServers())
                .userInfo(properties.getUsername(), properties.getPassword())
                .connectionListener(connectionListener)
                .errorListener(new NatsErrorListener())
                .maxReconnects(properties.getMaxReconnect())
                .reconnectWait(properties.getReconnectWait())
                .connectionTimeout(properties.getConnectionTimeout())
                .pingInterval(properties.getPingInterval())
                .reconnectBufferSize(properties.getReconnectBufferSize())
                .inboxPrefix(properties.getInboxPrefix());

        if (properties.isNoEcho()) {
            builder.noEcho();
        }

        return builder.build();
    }
}
