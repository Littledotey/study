package com.alibaba.sec.standard.business.base;

import com.alibaba.sec.standard.BusinessHandler;

/**
 * 抽象业务基类
 * @author  WTY
 * @date    2020/7/1 15:56
 */
public abstract class AbstractBusinessHandler<T> implements BusinessHandler<T> {

    @Override
    public Object handle(T t) throws Throwable {
        return null;
    }

    @Override
    public boolean support(T t) {
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
