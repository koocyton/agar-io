package com.doopp.iogame.service;

import com.doopp.iogame.pojo.Move;
import com.doopp.iogame.pojo.User;
import io.netty.channel.Channel;

import java.io.IOException;
import java.util.Map;

public interface GameService {

    Map<Integer, User> getUserMap();

    User channelUser(Channel channel);

    void onConnect(Channel channel, String token) throws IOException;

    void userJoin(User user, Channel channel);

    void userLeave(Channel channel);

    void receiveMessage(Channel channel, Move move);

    void runDaemon();
}
