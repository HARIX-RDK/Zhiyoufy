package com.example.zhiyoufy.common.elk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方便通过AOP进行截取的注解定义
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AsyncElkRecordable {
	String type();
	String[] tags() default {};
	boolean defaultOn() default false;
}
