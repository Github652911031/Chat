package model.user;

import common.domain.User;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private static Map<String,User> users ;
    private static Map<SocketChannel, String> onlineUsers;

    static{
        users = new HashMap<>();
        users.put("user1", new User("user1","pwd1"));
        users.put("user2", new User("user2","pwd2"));
        users.put("user3", new User("user3","pwd3"));
        users.put("user4", new User("user4","pwd4"));
        users.put("user5", new User("user5","pwd5"));
        onlineUsers = new ConcurrentHashMap<>();
    }

    public synchronized static boolean login(String username,String passwd,SocketChannel sc){
        if (!users.containsKey(username)) {
            return false;
        }
        User user = users.get(username);
        if (!user.getPasswd().equals(passwd)) {
            return false;
        }
        if(user.getUserChannel()!=null){
            System.out.println("重复登陆，拒绝请求");
            return false;
        }
        user.setUserChannel(sc);
        onlineUsers.put(sc,user.getUsername());
        return true;
    }

    public synchronized static boolean logout(String username){
        User user = users.get(username);
        SocketChannel sc = user.getUserChannel();
        onlineUsers.remove(sc);
        user.setUserChannel(null);
        return true;
    }

    public static String getOnlineUsers(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<SocketChannel, String> map : onlineUsers.entrySet()){
            sb.append(map.getValue());
            sb.append(" ");
        }
        return sb.toString();
    }

    public static SocketChannel getUserChannel(String username){
        User u = users.get(username);
        SocketChannel sc = u.getUserChannel();
        if(onlineUsers.get(sc) != null){
            return sc;
        }
        return null;
    }





}
