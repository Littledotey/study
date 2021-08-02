package com.alibaba.sec.standard.interceptor.base;

import java.util.ArrayDeque;
import java.util.Deque;

import com.alibaba.sec.standard.BusinessHandler;
import com.alibaba.sec.standard.HandlerInterceptor;

/**
 * 抽象拦截，扩展点只需要继承覆盖方法即可
 * @author  WTY
 * @date    2020/6/29 17:05
 */
public abstract class AbstractHandlerInterceptorAdaptor<T> implements HandlerInterceptor<T> {

    /** 让流程可以记录当前线程所持有的数据信息，后置器必须要清空，否则会概率性引发 OOM 等严重的错误 */
    protected ThreadLocal<Deque<Object>> threadLocal = new ThreadLocal<Deque<Object>>(){
        @Override
        protected Deque<Object> initialValue() {
            return new ArrayDeque<>();
        }
    };

    @Override
    public Object pre(BusinessHandler<T> handler, T t) {
        return null;
    }

    @Override
    public void post(BusinessHandler<T> handler, T t, Object result) throws Throwable {

    }

    @Override
    public void after(BusinessHandler<T> handler, T t, Throwable e) throws Throwable {

    }

    @Override
    public void hook(BusinessHandler<T> handler, T t) throws Throwable {

    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean support(T t) {
        return false;
    }
}
