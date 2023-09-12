package com.ysl.reigi.common;

/**
 * @description: 自定义业务异常
 * @author: YSL
 * @time: 2023/2/9 21:35
 */

public class CustomException extends RuntimeException{

    public CustomException(String message){

        super(message);
    }
}
