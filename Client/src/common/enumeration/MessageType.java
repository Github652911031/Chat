package common.enumeration;

public enum  MessageType {
    LOGIN(1,"登录"),
    LOGOUT(2,"注销"),
    REQUEST_ONLINEUSERS(3,"请求在线好友"),
    NORMAL(4,"单聊");

    private int code;
    private String  desc;

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
