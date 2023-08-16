package com.jkqj.nats;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NatsConnectionListener implements ConnectionListener {
    @Override
    public void connectionEvent(Connection connection, Events events) {
        log.info("NATS connection status changed " + events);
    }
}
