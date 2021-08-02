package com.alibaba.sec.standard.interceptor.base;

import java.util.List;

import com.alibaba.sec.standard.BusinessHandler;
import com.alibaba.sec.standard.HandlerInterceptor;
import com.alibaba.sec.standard.context.odps.JsonContent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * odps拦截处理执行器
 * @author  WTY
 * @date    2020/7/3 14:22
 */
@Component
public class OdpsInterceptorHandlerExecute extends AbstractInterceptorHandler<JsonContent> implements InitializingBean {

    /** 日志 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OdpsInterceptorHandlerExecute.class);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private List<HandlerInterceptor<JsonContent>> interceptors;

    public Object execute(BusinessHandler<JsonContent> businessHandler, JsonContent jsonContent){

        //result 用来装载 applyPre 返回的错误信息，例如参数校验不通过
        Object result = null;
        try{

            Object temp = this.applyPre(businessHandler, jsonContent);
            if(temp != null){
                //这里不为空就说明这里是有异常信息的，异常讯息直接返回
                return temp;
            }

            //业务运行和后置拦截的异常信息暂存
            Throwable t = null;
            try {
                //执行具体的业务
                result = businessHandler.handle(jsonContent);
                //业务后置处理
                this.applyPost(businessHandler , jsonContent , result);
            } catch (Throwable e) {
                LOGGER.error("OdpsInterceptorHandlerExecute execute error !" , e);
                //赋值异常信息
                t = e;
            }
            //后置处理器，异常信息处理，落库维护状态
            this.applyAfter(businessHandler , jsonContent , t);
        }catch(Throwable e){
            //这个异常属于后置异常中的异常，不属于框架处理范畴，选用日志输出
            LOGGER.error("OdpsInterceptorHandlerExecute error !" , e);
        }

        return result;
    }

    @Override
    public void afterPropertiesSet() {
        super.setInterceptors(interceptors);
    }
}
