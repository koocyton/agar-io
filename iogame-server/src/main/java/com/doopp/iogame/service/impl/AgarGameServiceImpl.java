package com.doopp.iogame.service.impl;

import com.doopp.gutty.json.MessageConverter;
import com.doopp.iogame.message.MyResponse;
import com.doopp.iogame.pojo.Element;
import com.doopp.iogame.pojo.Food;
import com.doopp.iogame.pojo.Move;
import com.doopp.iogame.pojo.User;
import com.doopp.iogame.service.GameService;
import com.doopp.gutty.annotation.Service;
import com.doopp.iogame.service.LoginService;
import com.doopp.iogame.util.IdWorker;
import com.google.common.collect.HashBasedTable;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Singleton
@Service("agarGameService")
public class AgarGameServiceImpl implements GameService {

    final private Map<Integer, User> userMap = new ConcurrentHashMap<>();

    final private HashBasedTable<Integer, Integer, Map<Integer, Integer>> userIndexTable = HashBasedTable.create();

    final private Map<Integer, Food> foodMap = new ConcurrentHashMap<>();

    final private HashBasedTable<Integer, Integer, Map<Integer, Integer>> foodIndexTable = HashBasedTable.create();

    final private Map<Integer, Channel> channelMap = new ConcurrentHashMap<>();

    final AtomicInteger foodId = new AtomicInteger(5163);

    @Inject
    private LoginService loginService;

    @Inject
    private MessageConverter jsonConverter;

    @Inject
    private IdWorker idWorker;

    @Inject
    private void initElement() {
        for(int x=0; x<200; x++){
            for(int y=0; y<200; y++){
                foodIndexTable.put(x, y, new ConcurrentHashMap<>());
                userIndexTable.put(x, y, new ConcurrentHashMap<>());
            }
        }
    }

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
        // 初始化数据
        user.initData();
        // 记录 user & channel 的索引
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

    public Food createFood() {
        if (foodMap.size()>=600) {
            return null;
        }
        if (idWorker.nextId()%100!=0) {
            return null;
        }
        Food food = new Food(foodId.getAndAdd(1));
        foodMap.put(food.id, food);
        Integer foodXIndex = (int) (food.x / 30);
        Integer foodYIndex = (int) (food.y / 30);
        foodIndexTable.get(foodXIndex, foodYIndex).put(food.id, food.id);
        return food;
    }

    private <T> void userMove(User moveUser, Move move) {
        double r = Math.sqrt(moveUser.grade/Math.PI);
        moveUser.x = move.x<r ? r : Math.min(move.x, 5000.00 - r);
        moveUser.y = move.y<r ? r : Math.min(move.y, 5000.00 - r);
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
        Food food = createFood();
        List<Food> cFood = collisionCheck();
        if (userMap.size()>0) {
            List<Element> elements = new ArrayList<>(userMap.values());
            if (food!=null) {
               elements.add(food);
            }
            if (cFood.size()>=1) {
                elements.addAll(cFood);
            }
            sendToAllUsers(elements);
        }
    }

    private List<Food> collisionCheck() {
        List<Food> removeFoodList = new ArrayList<>();
        userMap.forEach((k, user)->{
            double r = Math.ceil(Math.sqrt(user.grade/Math.PI));
            double leftX  = user.x-r;
            double rightX = user.x+r;
            double upY    = user.y+r;
            double downY  = user.y-r;
            int leftIdx   = (int) (leftX / 30);
            int rightIdx  = (int) (rightX / 30);
            int upIdx     = (int) (upY / 30);
            int downIdx   = (int) (downY / 30);
            // 范围查找
            for (int x=leftIdx; x<=rightIdx; x++) {
                for (int y=downIdx; y<=upIdx; y++) {
                    Map<Integer, Integer> foodIndexMap = foodIndexTable.get(x, y);
                    if (foodIndexMap==null) {
                        continue;
                    }
                    foodIndexMap.forEach((_id, id)->{
                        Food food = foodMap.get(id);
                        double dx = (user.x - food.x);
                        double dy = (user.y - food.y);
                        double dr = Math.sqrt(Math.abs(dx*dx + dy*dy));
                        if (dr<r) {
                            food.type = "remove-food";
                            removeFoodList.add(food);
                            foodMap.remove(food.id);
                            foodIndexMap.remove(food.id);
                            user.grade = user.grade + food.grade;
                        }
                    });
                }
            }
            userMap.forEach((_k, _other)->{
                if (k.equals(_k)) {
                    return;
                }
                if (Math.abs(user.grade - _other.grade)<_other.grade/10) {
                    return;
                }
                double dx = (user.x - _other.x);
                double dy = (user.y - _other.y);
                double dr = Math.sqrt(Math.abs(dx*dx + dy*dy));
                if (dr<r) {
                    User loser = (user.grade > _other.grade) ? _other : user;
                    loser.type = "remove-cell";
                    userMap.remove(loser.id);
                    Channel channel = channelMap.get(loser.id);
                    if (channel!=null && channel.isOpen()) {
                        sendToUser(loser, new MyResponse<>(user, 0, "you failed"));
                        channel.close();
                        if (user.grade > _other.grade) {
                            user.grade = user.grade + _other.grade;
                        }
                        else {
                            _other.grade = user.grade + _other.grade;
                        }
                    }
                }
            });
        });
        return removeFoodList;
    }
}
