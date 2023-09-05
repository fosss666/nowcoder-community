package com.fosss.community.exception;

/**
 * @author: fosss
 * Date: 2023/9/5
 * Time: 16:32
 * Description:
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
