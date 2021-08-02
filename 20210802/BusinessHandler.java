package com.alibaba.sec.standard;

import org.springframework.core.Ordered;

/**
 * 业务处理
 * @author  WTY
 * @date    2020/6/29 16:12
 */
public interface BusinessHandler<E> extends Supported<E>, Ordered {

    /**
     * handle - 业务性代码
     * @author  WTY
     * @date    2020/6/29 16:12
     * @param e 入参
     * @return  java.lang.Object
     */
    Object handle(E e) throws Throwable;

}
