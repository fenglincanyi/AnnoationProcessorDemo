package com.geng.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.geng.annotation.Bean.AttType.STRING;


/**
 * Created by gengjiarong
 * on 2017/12/3.
 * 生成标准的Javabean
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Bean {

    enum AttType {
        INT, FLOAT, DOUBLE, STRING
    }

    String clzName() default "";
    String[] attName() default {""};
    AttType[] typeName() default {STRING};
}
