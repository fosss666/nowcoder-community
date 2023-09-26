package com.fosss.community.constant;

/**
 * @author: fosss
 * Date: 2023/9/9
 * Time: 20:16
 * Description:
 */
public enum ResultEnum {
    SUCCESS(0, "成功"),
    FOLLOW_SUCCESS(0, "关注成功！"),
    UNFOLLOW_SUCCESS(0, "取关成功！"),

    USER_NOT_FOUND(1, "用户名不存在!"),

    PERMISSION_ERROR(403, "您还没有登录哦！"),

    EMAIL_NULL(2, "邮箱不能为空！"),

    EMAIL_NOT_REGISTER(3, "此邮箱暂未注册，请前往注册页！"),

    SERVER_EXCEPTION(4, "服务器异常！");

    //code
    public int code;
    //msg
    public String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
