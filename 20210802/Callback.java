package com.alibaba.sec.standard;

/**
 * 回调
 * @author  WTY
 * @date    2020/8/4 18:41
 */
public interface Callback<T,R> {

    /**
     * 具体的回调信息
     * */
    R callback(T t) ;

}