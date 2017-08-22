/**
 * 消息对象示例，将需要一次性传递的信息封装进去；
 * 可以定义多个这样的消息对象，在不同场合传递使用
 * <p>
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:EventBusDEMO
 * <br/>Date:Aug，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

 public class MessageEvent {
    private String msg;

    public MessageEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
