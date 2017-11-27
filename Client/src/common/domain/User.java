package common.domain;

import java.nio.channels.SocketChannel;

public class User {
    private String username;
    private String passwd;
    private SocketChannel userChannel;


    public User(){

    }
    public User(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public SocketChannel getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(SocketChannel userChannel) {
        this.userChannel = userChannel;
    }
}
