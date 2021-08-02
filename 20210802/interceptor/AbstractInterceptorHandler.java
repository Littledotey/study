package com.alibaba.sec.standard.interceptor.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.sec.standard.BusinessHandler;
import com.alibaba.sec.standard.HandlerInterceptor;
import com.alibaba.sec.standard.lock.LockResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象拦截处理器
 * @author  WTY
 * @date    2020/7/3 14:21
 */
@SuppressWarnings("Duplicates")
public abstract class AbstractInterceptorHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInterceptorHandler.class);

    private volatile int index = -1;

    private List<HandlerInterceptor<T>> interceptors = Collections.synchronizedList(new ArrayList<>());

    private static volatile LockResource lock = new LockResource();

    /**
     * 顺序执行，比如 1,2,3 接口，就按照 1,2,3的顺序去走
     * */
    Object applyPre(BusinessHandler<T> businessHandler , T t){
        for(int i = 0 ; i < interceptors.size() ; i++ ){
            HandlerInterceptor<T> interceptor = interceptors.get(i);
            if(!interceptor.support(t)){
                continue;
            }
            if(!interceptor.available(t)){
                return null;
            }
            this.index = i;
            Object pre = interceptor.pre(businessHandler,t);
            if(pre != null){
                applyAfter(businessHandler,t,null);
                return pre;
            }
        }
        return null;
    }

    /**
     * 拦截器数量逆序执行，比如 1,2,3 接口就 3,2,1 顺序执行
     * */
    void applyPost(BusinessHandler<T> businessHandler , T t, Object result) throws Throwable {
        for(int i = interceptors.size() - 1 ; i >= 0 ; i--){
            HandlerInterceptor<T> interceptor = interceptors.get(i);
            if(!interceptor.support(t)){
                continue;
            }
            if(!interceptor.available(t)){
                return;
            }
            LOGGER.info(interceptor.getClass().getName() + " 拦截器正在执行！");
            interceptor.hook(businessHandler,t);
            interceptor.post(businessHandler,t,result);
        }
    }

    /**
     * 最终执行按照执行的下标长度去逆序执行，方便一些不走 support 和 available 的公共拦截器执行
     * */
    void applyAfter(BusinessHandler<T> businessHandler , T t , Throwable throwable){

        for(int i = this.index ; i >= 0 ; i--){
            HandlerInterceptor<T> interceptor = interceptors.get(i);
            if(!interceptor.support(t)){
                continue;
            }
            if(!interceptor.available(t)){
                return ;
            }
            try {
                interceptor.after(businessHandler , t , throwable);
            } catch (Throwable e) {
                LOGGER.error("AbstractInterceptorHandler applyAfter after() error !" , e);
            }
        }
    }

    /**
     * 选择这里赋值是为了排序，不信任 Spring 的 Bean 排序：
     * 1：多 Bean 都有 order ，无法精确定位自有 Bean 的确切执行顺序
     * 2：自己管理便于维护，断点可以看到自有 Bean 的类型，便于筛选过滤
     * */
    void setInterceptors(List<HandlerInterceptor<T>> interceptors){
        lock.getLock().lock();
        try{
            this.interceptors = interceptors;
            this.interceptors.sort(new Comparator<HandlerInterceptor<T>>() {
                @Override
                public int compare(HandlerInterceptor<T> o1, HandlerInterceptor<T> o2) {
                    return o2.getOrder() - o1.getOrder();
                }
            });
        }catch(Exception e){
            LOGGER.error(" AbstractInterceptorHandler setInterceptors error ! " , e);
        }finally {
            lock.getLock().unlock();
        }
    }

}
