package me.linx.vchat.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* me.linx.vchat.controller.biz..*.*(..))")
    public void bizPointcut() {
    }

    @Pointcut("execution(* me.linx.vchat.controller.biz.UploadController.*(..))")
    public void uploadPointcut() {
    }
}
