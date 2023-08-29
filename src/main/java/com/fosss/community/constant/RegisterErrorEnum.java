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
public enum RegisterErrorEnum {
    USERNAME_NULL("usernameMsg","账号不能为空!"),
    PASSWORD_NULL("passwordMsg","密码不能为空!"),
    EMAIL_NULL("emailMsg","邮箱不能为空!"),
    USERNAME_EXIST("usernameMsg","该账号已存在!"),
    EMAIL_EXIST("emailMsg","该邮箱已存在!");


    private String key;
    private String msg;

}
