package com.gao.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * Created by 20170707365 on 2018/4/20.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GRequestParam {
    String value() default "";
}
