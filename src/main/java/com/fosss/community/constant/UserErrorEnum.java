package com.fosss.community.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author: fosss
 * Date: 2023/8/29
 * Time: 22:20
 * Description:
 */
@Getter
@AllArgsConstructor
public enum UserErrorEnum {
    USERNAME_NULL("usernameMsg", "账号不能为空!"),
    PASSWORD_NULL("passwordMsg", "密码不能为空!"),
    EMAIL_NULL("emailMsg", "邮箱不能为空!"),

    USERNAME_EXIST("usernameMsg", "该账号已存在!"),
    EMAIL_EXIST("emailMsg", "该邮箱已存在!"),

    USERNAME_NOT_EXIST("usernameMsg", "账号不存在！"),

    USERNAME_NOT_ACTIVATION("usernameMsg", "该账号未激活！"),

    PASSWORD_ERROR("passwordMsg", "密码错误！"),

    CODE_ERROR("codeMsg", "验证码错误！");

    private String key;
    private String msg;

}
