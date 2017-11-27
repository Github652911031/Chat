package common.enumeration;

public enum  ResponseCode {
    LOGIN_SUCCESS(1,"登录成功"),
    LOGIN_FAILURE(2,"登录失败");
//    LOGOUT_SUCCESS(3,"下线成功");

    private int code;
    private String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
