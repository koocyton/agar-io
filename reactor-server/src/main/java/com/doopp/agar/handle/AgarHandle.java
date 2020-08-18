package com.doopp.agar.handle;

import com.doopp.agar.api.service.UserService;
import com.doopp.agar.pojo.Food;
import com.doopp.agar.pojo.User;
import com.doopp.agar.utils.IdWorker;
import com.doopp.agar.utils.JsonUtil;
import com.doopp.reactor.guice.RequestAttribute;
import com.doopp.reactor.guice.websocket.AbstractWebSocketServerHandle;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.ws.rs.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Path("/ws/agar-io")
@Slf4j
@Singleton
public class AgarHandle extends AbstractWebSocketServerHandle {

    private final Map<Long, User> players = new ConcurrentHashMap<>();

    private final Map<Long, Channel> channels = new ConcurrentHashMap<>();

    private final Map<Long, Food> foods = new ConcurrentHashMap<>();

    private final Map<Long, Food> removeFoods = new ConcurrentHashMap<>();

    private final AttributeKey<Long> userIdAttributeKey = AttributeKey.valueOf("UserId");

    private final static double PI = 3.1415926;

    @Inject
    private UserService userService;

    @Override
    public String secWebSocketProtocol(HttpServerRequest request) {
        return "User-Token";
    }

    @Override
    public Mono<Void> onConnect(Channel channel) {
        RequestAttribute requestAttribute = channel.attr(RequestAttribute.REQUEST_ATTRIBUTE).get();
        User user = requestAttribute.getAttribute("SessionUser", User.class);
        addPlayer(channel, user);
        pushFoods(channel);
        return Mono.empty();
    }

    @Override
    protected Mono<Void> onTextMessage(TextWebSocketFrame frame, Channel channel) {
        User user = getChannelUser(channel);
        if (user!=null && !Strings.isNullOrEmpty(frame.text()) && frame.text().contains(" ")) {
            String[] xy = frame.text().split(" ");
            user.setX(Integer.parseInt(xy[0]));
            user.setY(Integer.parseInt(xy[1]));
            players.put(user.getId(), forage(user));
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> onClose(CloseWebSocketFrame frame, Channel channel) {
        removePlayer(channel);
        return super.onClose(frame, channel);
    }

    public void pushPlayers() {
        String playersString = playersToString();
        String removeFoodsString = removeFoodsToString();
        channels.forEach((userId, channel)->{
            if (channel.isOpen()) {
                this.sendTextMessage(playersString + "\n" +  removeFoodsString, channel);
            }
        });
        for(Food food : this.removeFoods.values()) {
            this.removeFoods.remove(food.getId());
        }
    }

    public void pushFood() {
        if (this.foods.size()>10000) {
            return;
        }
        Food food = userService.creatFood();
        this.foods.put(food.getId(), food);
        channels.forEach((userId, channel)->{
            if (channel.isOpen()) {
                this.sendTextMessage(food.toString(), channel);
            }
        });
    }

    public void pushFoods(Channel channel) {
        String foods = foodsToString();
        if (channel.isOpen()) {
            this.sendTextMessage(foods, channel);
        }
    }

    private User forage(User user) {
        double r = Math.sqrt(user.getGrade()/PI);
        for(Food food : foods.values()) {
            double distance = Math.sqrt(
                    Math.abs(
                            (user.getX() - food.getX()) * (user.getX() - food.getX())
                            + (user.getY() - food.getY()) * (user.getY() - food.getY())
                    )
            );
            if (distance<=r) {
                foods.remove(food.getId());
                removeFoods.put(food.getId(), food);
                user.setGrade(user.getGrade() + food.getGrade());
            }
        }

        for(User player : players.values()) {
            double distance = Math.sqrt(
                    Math.abs(
                            (user.getX() - player.getX()) * (user.getX() - player.getX())
                                    + (user.getY() - player.getY()) * (user.getY() - player.getY())
                    )
            );
            if (distance<=r && user.getGrade()>player.getGrade()+1000) {
                players.remove(player.getId());
                user.setGrade(user.getGrade() + player.getGrade());
            }
        }
        return user;
    }

    private String removeFoodsToString() {
        StringBuilder s = new StringBuilder();
        for(Food food : removeFoods.values()) {
            food.setType("remove-food");
            s.append(food.toString());
        }
        return s.toString();
    }

    private String playersToString() {
        StringBuilder s = new StringBuilder();
        for(User user : players.values()) {
            s.append("\n").append(user.toString());
        }
        return s.toString();
    }

    private String foodsToString() {
        StringBuilder s = new StringBuilder();
        for(Food food : foods.values()) {
            s.append("\n").append(food.toString());
        }
        return s.toString();
    }

    private Long getChannelUserId(Channel channel) {
        return channel.attr(userIdAttributeKey).get();
    }

    private void setChannelUser(Channel channel, User user) {
        channel.attr(userIdAttributeKey).set(user.getId());
    }

    private User getChannelUser(Channel channel) {
        Long userId = getChannelUserId(channel);
        if (userId==null) {
            return null;
        }
        return players.get(userId);
    }

    private void addPlayer(Channel channel, User user) {
        players.put(user.getId(), user);
        channels.put(user.getId(), channel);
        setChannelUser(channel, user);
    }

    private void removePlayer(Channel channel) {
        Long userId = getChannelUserId(channel);
        if (userId!=null) {
            players.remove(userId);
            channels.remove(userId);
        }
    }
}
