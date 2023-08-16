package com.jkqj.nats;

import io.nats.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
public class ConnectionCache implements ConnectionListener {
    public static final int CONNECT_WAIT_TIME = 3;
    private final List<Connection> connections;
    private BiConsumer<Connection, ConnectionListener.Events> eventsHandler;

    public ConnectionCache(int size, ConnectionAllocator connectionAllocator) {
        this.connections = new ArrayList<>(size);
        initConnections(size, connectionAllocator);
        log.debug("initialized cache, size {}", size);
    }

    private void initConnections(int size, ConnectionAllocator connectionAllocator) throws IllegalStateException {
        for (int i = 0; i < size; i++) {
            var connection = connectionAllocator.allocate(this);
            if (connection.getStatus() != Connection.Status.CONNECTED) {
                try {
                    Thread.sleep(Duration.ofSeconds(CONNECT_WAIT_TIME).toMillis());
                } catch (InterruptedException e) {
                    log.warn("create connection error", e);
                }

                if (connection.getStatus() != Connection.Status.CONNECTED) {
                    throw new IllegalStateException("connection failed");
                }
            }

            connections.add(connection);
        }
    }

    public Connection conn() throws IllegalStateException {
        var connection = pickConnection();
        if (connection.getStatus() != Connection.Status.CONNECTED) {
            try {
                Thread.sleep(Duration.ofSeconds(CONNECT_WAIT_TIME).toMillis());
            } catch (InterruptedException e) {
                log.debug("get conn error", e);
            }
            connection = pickConnection();
            if (connection.getStatus() != Connection.Status.CONNECTED) {
                throw new IllegalStateException("connection disconnected");
            }
        }
        return connection;
    }

    private Connection pickConnection() {
        return connections.get(RandomUtils.nextInt(0, connections.size()));
    }

    public JetStreamManagement jsm() {
        try {
            return conn().jetStreamManagement();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JetStream js() {
        try {
            return conn().jetStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyValueManagement kvm() {
        try {
            return conn().keyValueManagement();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (InterruptedException e) {
                log.warn("connection close error", e);
            }
        }
    }

    public void addEventHandler(BiConsumer<Connection, ConnectionListener.Events> eventsHandler) {
        this.eventsHandler = eventsHandler;
    }

    @Override
    public void connectionEvent(Connection connection, Events events) {
        log.debug("got event {}", events);
        if (this.eventsHandler != null) {
            this.eventsHandler.accept(connection, events);
        }
    }
}
