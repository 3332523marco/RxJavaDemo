package shengyuan.rxjavademo.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 *      Retention注解的功能差不多说明的就是你的注解的生命周期吧，就是什么时候失效，它的值如下
 *         1.SOURCE:在源文件中有效（即源文件保留）
 *  　　　　2.CLASS:在class文件中有效（即class保留）
 *  　　　　3.RUNTIME:在运行时有效（即运行时保留）
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PoetryQualifier {//注解的定义
    String value() default "";
}