package com.doopp.agar.server.socket;

import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Hybi51Handshake extends Hybi13Handshake {

    protected final Set<String> subprotocols = new HashSet<>();

    public Hybi51Handshake() {
        super();
        this.subprotocols.add("User-Token");
    }

    @Override
    protected String supportedSubprotols(String[] requestedSubprotocolArray) {
        for (String p : requestedSubprotocolArray) {
            String requestedSubprotocol = p.trim();

            for (String supportedSubprotocol : subprotocols) {
                if (requestedSubprotocol.equals(supportedSubprotocol)) {
                    return supportedSubprotocol;
                }
            }
        }
        return null;
    }
}

