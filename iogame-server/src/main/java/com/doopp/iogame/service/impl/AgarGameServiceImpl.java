package com.doopp.iogame.service.impl;

import com.doopp.gutty.json.MessageConverter;
import com.doopp.iogame.message.MyResponse;
import com.doopp.iogame.pojo.Food;
import com.doopp.iogame.pojo.Move;
import com.doopp.iogame.pojo.User;
import com.doopp.iogame.service.GameService;
import com.doopp.gutty.annotation.Service;
import com.doopp.iogame.service.LoginService;
import com.doopp.iogame.util.IdWorker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Singleton
@Service("agarGameService")
public class AgarGameServiceImpl implements GameService {

    final private Map<Integer, User> userMap = new ConcurrentHashMap<>();

    final private Map<Integer, Food> foodMap = new ConcurrentHashMap<>();

    final private Map<Integer, Channel> channelMap = new ConcurrentHashMap<>();

    final AtomicInteger foodId = new AtomicInteger(5163);

    @Inject
    private LoginService loginService;

    @Inject
    private MessageConverter jsonConverter;

    @Inject
    private IdWorker idWorker;

    @Override
    public Map<Integer, User> getUserMap() {
        return userMap;
    }

    @Override
    public void userJoin(User user, Channel channel) {
        if (channel==null || !channel.isOpen()) {
            return;
        }
        // 用户重复登录，断开旧连接
        userLeave(user.id);
        // 注册到 channel 信息里
        channel.attr(AttributeKey.<Integer>valueOf("UserId")).set(user.id);
        // 记录 user & channel 的索引
        user.grade = 3999;
        user.type = "cell";
        user.x = (int) (Math.random()*0x1500);
        user.y = (int) (Math.random()*0x1500);
        user.time = System.currentTimeMillis();
        int _color = (int)(Math.random() * 0x1000000);
        user.color = Integer.toHexString(_color%256)
                + Integer.toHexString(_color/256%256)
                + Integer.toHexString(_color/256/256%256);

        userMap.put(user.id, user);
        channelMap.put(user.id, channel);
        sendToUser(user, new MyResponse<>(user, 0, "you connected"));
        sendToUser(user, foodMap.values());
    }

    @Override
    public void userLeave(Channel channel) {
        User user = channelUser(channel);
        if (user!=null && user.id!=null) {
            userLeave(user.id);
        }
    }

    private void userLeave(Integer userId) {
        userMap.remove(userId);
        Channel channel = channelMap.get(userId);
        if (channel!=null && channel.isOpen()) {
            channel.close();
        }
        channelMap.remove(userId);
    }

    @Override
    public void onConnect(Channel channel, String token) {
        User user = loginService.userLogin(token);
        if (user==null) {
            if (channel.isOpen()) {
                channel.writeAndFlush(new TextWebSocketFrame("disconnect !"));
                channel.close();
            }
            return;
        }
        this.userJoin(user, channel);
    }

    @Override
    public User channelUser(Channel channel) {
        Integer userId = channel.attr(AttributeKey.<Integer>valueOf("UserId")).get();
        if (userId==null) {
            return null;
        }
        return userMap.get(userId);
    }

    @Override
    public void receiveMessage(Channel channel, Move move) {
        User user = channelUser(channel);
        userMove(user, move);
    }

    public void createFood() {
        if (foodMap.size()>=500) {
            return;
        }
        if (idWorker.nextId()%1000!=0) {
            return;
        }
        int color = (int)(Math.random() * 0x1000000);
        String rgb = Integer.toHexString(color%256)
                + Integer.toHexString(color/256%256)
                + Integer.toHexString(color/256/256%256);
        Food food = new Food();
        food.setId(foodId.getAndAdd(1));
        food.setType("food");
        food.setGrade(1000);
        food.setColor(rgb);
        food.setX((int)(Math.random() * 0x1500));
        food.setY((int)(Math.random() * 0x1500));
        foodMap.put(food.getId(), food);
        sendToAllUsers(new ArrayList<Food>(){{add(food);}});
    }

    private <T> void userMove(User moveUser, Move move) {
        moveUser.x = move.x<0 ? 0 : move.x>5000 ? 5000 : move.x;
        moveUser.y = move.y<0 ? 0 : move.y>5000 ? 5000 : move.y;
        moveUser.time = System.currentTimeMillis();
    }

    private void sendToUser(User toUser, Object obj) {
        Channel channel = channelMap.get(toUser.id);
        if (channel!=null && channel.isOpen()) {
            channel.writeAndFlush(new TextWebSocketFrame(
                (obj instanceof String) ? (String) obj : jsonConverter.toJson(obj)
            ));
        }
    }

    private void sendToAllUsers(Object obj) {
        userMap.forEach((uid, user)-> sendToUser(user, obj));
    }

    @Override
    public void runDaemon() {
        createFood();
        if (userMap.size()>0) {
            sendToAllUsers(userMap.values());
        }
    }
}
