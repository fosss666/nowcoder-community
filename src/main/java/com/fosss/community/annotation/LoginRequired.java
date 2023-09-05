package com.fosss.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: fosss
 * Date: 2023/9/5
 * Time: 19:07
 * Description:
 */
@Target(ElementType.METHOD)//注解放在方法上
@Retention(RetentionPolicy.RUNTIME)//运行时有效
public @interface LoginRequired {
}
