package me.linx.vchat.aop;

import me.linx.vchat.bean.User;
import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.controller.biz.BaseBizController;
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
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Order(2)
@Component
public class UploadActionHandlerAop implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${upload-dir}")
    private String uploadDir;

    private class Handler {
        Object bean;
        Method method;

        Handler(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }
    }

    private Map<String, List<Handler>> map = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 根容器为Spring容器
        if (event.getApplicationContext().getParent() == null) {
            // 所有Service
            Map<String, Object> beans = event.getApplicationContext().getBeansWithAnnotation(Service.class);

            for (Object bean : beans.values()) {
                String fullName = bean.getClass().getName();
                String name = fullName.substring(0, fullName.indexOf("Service") + "Service".length());
                try {
                    //Spring代理类无法获取方法上的注解，这里通过反射从真实类中获取
                    Class<?> aClass = getClass().getClassLoader().loadClass(name);
//                    Class<?> aClass = Objects.requireNonNull(ClassLoader.getSystemClassLoader()).loadClass(name);

                    // 所有方法
                    Method[] methods = aClass.getDeclaredMethods();
                    for (Method method : methods) {
                        UploadAction uploadAction = method.getDeclaredAnnotation(UploadAction.class);

                        if (uploadAction != null) {
                            String actionName = uploadAction.action();
                            if (StringUtils.isNotTrimEmpty(actionName)) {
                                List<Handler> handlerList = map.get(actionName);

                                // 实际的操作要回到Spring代理类
                                Method realMethod = null;
                                for (Method m : bean.getClass().getDeclaredMethods()) {
                                    if (m.getName().equals(method.getName())) {
                                        realMethod = m;
                                        break;
                                    }
                                }

                                if (handlerList == null) {
                                    handlerList = new ArrayList<>();

                                    handlerList.add(new Handler(bean, realMethod));
                                    map.put(actionName, handlerList);
                                } else {
                                    handlerList.add(new Handler(bean, realMethod));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
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

                //获取action
                String action = request.getHeader("action");
                try {
                    result = point.proceed();

                    if (result instanceof JsonResult) {
                        JsonResult js = (JsonResult) result;

                        if (js.getCode() == CodeMap.Yes.value) {
                            String fileName = js.getData().toString();
                            List<Handler> handlerList = map.get(action);

                            Object target = point.getTarget();
                            User user = null;
                            if (target instanceof BaseBizController) {
                                BaseBizController controller = (BaseBizController) target;

                                user = controller.getCurrentUser();
                            }

                            if (user != null && handlerList != null && !handlerList.isEmpty()) {
                                for (Handler handler : handlerList) {
                                    Object bean = handler.bean;
                                    Method method = handler.method;
                                    method.setAccessible(true); //不需要安全检查
                                    result = method.invoke(bean, fileName, user);
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
            e.printStackTrace();
        }

        return result;
    }
}
