package com.gyx.floodmyth.aspect;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Limiter {

    /**
     * Limiter id
     */
    String value() default "";

    /**
     * 令牌消耗数量
     */
    int num() default 1;

    /**
     * 回调方法
     */
    String fallback() default "";

}
