package com.fosss.community.constant;

/**
 * @author: fosss
 * Date: 2023/9/9
 * Time: 20:16
 * Description:
 */
public enum ResultEnum {
    SUCCESS(0,"成功"),
    PERMISSION_ERROR(403,"您还没有登录哦！");

    //code
    public int code;
    //msg
    public String msg;

    ResultEnum(int code, String msg){
        this.code=code;
        this.msg=msg;
    }

}
