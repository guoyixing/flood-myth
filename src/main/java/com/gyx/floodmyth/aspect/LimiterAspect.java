package com.gyx.floodmyth.aspect;


import com.gyx.floodmyth.core.limiter.LimiterHandler;
import com.gyx.floodmyth.core.server.RegisterServer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Aspect
public class LimiterAspect {

    @Pointcut("@annotation(com.gyx.floodmyth.aspect.Limiter)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(limiter)")
    public Object around(ProceedingJoinPoint pjp, Limiter limiter) throws Throwable {
        LimiterHandler rateLimiter = RegisterServer.get(limiter.value());
        if (rateLimiter.tryAccess(limiter.num())) {
            return pjp.proceed();
        }

        //快速失败后的回调方法
        return fallback(pjp, limiter);
    }

    /**
     * 快速失败的回调方法
     * <p>
     * 回调方法必须和注解注释的方法在同一个类中，并且参数完全一致
     *
     * @param pjp     切入点
     * @param limiter 注解数据
     */
    private Object fallback(ProceedingJoinPoint pjp, Limiter limiter) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Signature sig = pjp.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("此注解只能使用在方法上");
        }
        //回调方法必须和注解注释的方法在同一个类中，并且参数完全一致
        MethodSignature msg = (MethodSignature) sig;
        Object target = pjp.getTarget();
        Method fallback = target.getClass().getMethod(limiter.fallback(), msg.getParameterTypes());
        return fallback.invoke(target, pjp.getArgs());
    }

}
