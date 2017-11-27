package common.enumeration;

public enum ResponseType {
    NORMAL(1,"消息"),
    PROMPT(2,"提示"),
    NOTIFY(3,"上线通知"),
    ONLINEUSERS(4,"在线用户列表"),
    NOTIFY_OUT(5,"下线通知");

    private int code;
    private String  desc;

    ResponseType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
