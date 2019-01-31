package me.linx.vchat.aop;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.controller.biz.BaseBizController;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.utils.JwtUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Order(1)
@Component
public class TokenVerifyAop {

    @Around("me.linx.vchat.aop.Pointcuts.bizPointcut()")
    public Object interceptor(ProceedingJoinPoint point) {
        //正在被通知的方法相关信息
//        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取被拦截的方法
//        Method method = signature.getMethod();
        //获取被拦截的方法名
//        String methodName = method.getName();

        try{
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //返回的结果
        Object result = null;
        //返回方法参数
        Object[] args = point.getArgs();
        for (Object arg : args) {
            //获取request请求
            if (arg instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) arg;
                String token = request.getHeader("token");

                Long userId = JwtUtils.verify(token);
                if (userId == null) {
                    result = getFailureResult();
                }else {
                    Object target = point.getTarget();
                    if (target instanceof BaseBizController){
                        BaseBizController controller = (BaseBizController) target;
                        controller.setCurrentUserId(userId);
                    }
                }
            }
        }
        try {
            if (result == null){
                // 一切正常的情况下，继续执行被拦截的方法
                result = point.proceed();
            }
        } catch (Throwable e) {
            result = new JsonResult(CodeMap.ErrorSys);
        }

        return result;
    }

    private JsonResult getFailureResult() {
        return new JsonResult(CodeMap.ErrorTokenFailed);
    }
}
