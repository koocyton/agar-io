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

    private int timer = 0;

    public synchronized void run() {
        agarHandle.playersOnMove();
        agarHandle.pushPlayers();
        timer++;
        if (timer>100) {
            agarHandle.pushFood();
            timer= 0;
        }
        // 用户移动
        // new Thread(() -> agarHandle.playersOnMove()).start();
    }
}
