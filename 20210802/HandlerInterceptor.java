package com.alibaba.sec.standard;

import org.springframework.core.Ordered;

/**
 * 流程链路规范
 * @author  WTY
 * @date    2020/6/29 16:13
 */
public interface HandlerInterceptor<T> extends Supported<T>, Available<T>, Ordered {

    /**
     * pre valid ..
     * @author  WTY
     * @date    2020/6/29 16:13
     * @param handler 具体业务
     * @param t 入参
     * @return  java.lang.Object
     */
    Object pre(BusinessHandler<T> handler, T t);

    /**
     * post handler after
     * @author  WTY
     * @date    2020/6/29 16:14
     * @param handler 具体业务
     * @param t 入参
     * @param result pre结果
     * @throws Throwable 异常
     */
    void post(BusinessHandler<T> handler, T t, Object result) throws Throwable;

    /**
     * after exception ..
     * @author  WTY
     * @date    2020/6/29 16:14
     * @param handler 具体业务
     * @param t 入参
     * @param e 异常
     * @throws Throwable 异常
     */
    void after(BusinessHandler<T> handler, T t, Throwable e) throws Throwable;

    /**
     * 钩子 - 耦合于 post 过程中，可以做一些上下文的修改，过期数据剔除。
     * @author  WTY
     * @date    2020/6/29 16:15
     * @param handler 具体业务
     * @param t 入参
     */
    void hook(BusinessHandler<T> handler, T t) throws Throwable;

    /**
     * 初始化顺序
     * @author  WTY
     * @date    2020/6/29 16:15
     * @return  int
     */
    @Override
    int getOrder();
}
