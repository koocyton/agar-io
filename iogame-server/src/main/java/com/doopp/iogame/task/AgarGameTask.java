package com.doopp.iogame.task;

import com.doopp.iogame.service.GameService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class AgarGameTask implements Runnable {

    @Inject
    @Named("agarGameService")
    private GameService agarGameService;

    public synchronized void run() {
        agarGameService.runDaemon();
    }
}
