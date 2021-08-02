package com.alibaba.sec.standard;

/**
 * 开关规范
 * @author  WTY
 * @date    2020/6/29 16:13
 */
public interface Available<E> {

    /**
     * 开关 - 目前没有正式用到，大多数扩展点都是返回 true，开关系统后续可以自制一个或接入集团其他的工具
     * @author  WTY
     * @date    2020/6/29 16:13
     * @param e 入参
     * @return  boolean
     */
    default boolean available(E e){
        return true;
    }

}
