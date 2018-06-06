package com.tim.syslog;

import java.lang.annotation.*;

/**
 * @description: 自定义注解 拦截Controller
 * @author: lizhiming
 * @since: 2017/11/30
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysControllerLog {
    String desc() default "";
}
