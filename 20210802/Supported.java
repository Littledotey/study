package com.alibaba.sec.standard;

/**
 * 支持规范
 * @author  WTY
 * @date    2020/6/29 16:13
 */
public interface Supported<E> {

    /**
     * 支持 - 目前纯 if else 逻辑性判断，后续可以接入规则管理系统。
     * @author  WTY
     * @date    2020/6/29 16:13
     * @param e 入参
     * @return  boolean
     */
    boolean support(E e);

}
