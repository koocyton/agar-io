package com.doopp.agar.task;

import com.doopp.agar.handle.AgarHandle;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class AgarTask implements Runnable {

    @Inject
    private AgarHandle agarHandle;

    public synchronized void run() {
        agarHandle.pushPlayers();
        // agarHandle.addFood();
    }
}
