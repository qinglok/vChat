package me.linx.vchat.aop;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Order(2)
@Component
public class UploadActionHandlerAop implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${upload-dir}")
    private String uploadDir;

    private class Handler {
        Object bean;
        Method method;

        public Handler(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }
    }

    private Map<String, List<Handler>> map = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 根容器为Spring容器
        if (event.getApplicationContext().getParent() == null) {
            Map<String, Object> beans = event.getApplicationContext().getBeansWithAnnotation(Service.class);
            for (Object bean : beans.values()) {
                Method[] methods = bean.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    Annotation action = method.getAnnotation(UploadAction.class);
                    if (action != null) {
                        String actionName = ((UploadAction) action).action();
                        if (StringUtils.isNotTrimEmpty(actionName)) {
                            List<Handler> handlerList = map.get(actionName);
                            if (handlerList == null) {
                                handlerList = new ArrayList<>();
                                handlerList.add(new Handler(bean, method));
                                map.put(actionName, handlerList);
                            } else {
                                handlerList.add(new Handler(bean, method));
                            }
                        }
                    }
                }
            }
            System.err.println("=====ContextRefreshedEvent=====" + event.getSource().getClass().getName());
        } else {
            System.out.println("getParent()!=null");
        }
    }

    @Around("me.linx.vchat.aop.Pointcuts.uploadPointcut()")
    public Object interceptor(ProceedingJoinPoint point) {
        //正在被通知的方法相关信息
//        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取被拦截的方法
//        Method method = signature.getMethod();
        //获取被拦截的方法名
//        String methodName = method.getName();
        //返回的结果
        Object result = null;
        //返回方法参数
        Object[] args = point.getArgs();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) arg;
                Object userId = request.getSession().getAttribute("currentUserId");
                System.out.println("--->>> " + (userId == null ? "" : userId));

                //获取action
                String action = request.getHeader("action");
                try {
                    result = point.proceed();

                    if (result instanceof JsonResult) {
                        JsonResult js = (JsonResult) result;
                        if (js.getCode() == CodeMap.Yes.value) {
                            String fileName = js.getData().toString();
                            List<Handler> handlerList = map.get(action);

                            if (userId != null && handlerList != null && !handlerList.isEmpty()) {
                                for (Handler handler : handlerList) {
                                    Object bean = handler.bean;
                                    Method method = handler.method;

                                    result = method.invoke(bean, fileName, userId);
                                }
                            }
                        }
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        try {
            if (result == null)
                // 一切正常的情况下，继续执行被拦截的方法
                result = point.proceed();
        } catch (Throwable e) {
//            result = new JsonResult(CodeMap.ErrorSys);
        }

        return result;
    }
}
