package com.gao.spring.annotation;

import java.lang.annotation.*;

/**
 * Created by 20170707365 on 2018/4/20.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GAutowired {
    String value() default "";
}
