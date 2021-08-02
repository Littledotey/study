package com.alibaba.sec.standard.interceptor.base;

import java.util.List;

import com.alibaba.sec.standard.BusinessHandler;
import com.alibaba.sec.standard.HandlerInterceptor;
import com.alibaba.sec.standard.context.monitor.MonitorContent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监控 拦截处理执行器
 * @author  WTY
 * @date    2020/8/7 13:42
 */
@Component
public class OdpsMonitorInterceptorHandlerExecute extends AbstractInterceptorHandler<MonitorContent> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OdpsMonitorInterceptorHandlerExecute.class);

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private List<HandlerInterceptor<MonitorContent>> interceptors;

    public Object execute(BusinessHandler<MonitorContent> businessHandler, MonitorContent jsonContent){

        Object result = null;
        try{

            Object temp = this.applyPre(businessHandler, jsonContent);
            if(temp != null){
                LOGGER.info("OdpsMonitorInterceptorHandlerExecute validation fail ! because : {}" , temp);
                return temp;
            }

            Throwable t = null;

            try {
                result = businessHandler.handle(jsonContent);
                this.applyPost(businessHandler , jsonContent , result);
            } catch (Throwable e) {
                LOGGER.error("DataSourceMonitorHandlerExecute execute error !" , e);
                t = e;
            }
            this.applyAfter(businessHandler , jsonContent , t);
        }catch(Throwable e){
            LOGGER.error("DataSourceMonitorHandlerExecute error !" , e);
        }

        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setInterceptors(interceptors);
    }
}
