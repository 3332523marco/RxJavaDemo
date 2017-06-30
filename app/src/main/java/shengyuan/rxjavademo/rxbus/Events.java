package shengyuan.rxjavademo.rxbus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * Created by Marco on 17/2/14.
 */
public class Events<T> {

    //所有事件的CODE
    public static final int TAP = 1; //点击事件
    public static final int OTHER = 21; //其他事件

    /**
     *   官方文档说明，安卓开发应避免使用Enum（枚举类），因为相比于静态常量Enum会花费两倍以上的内存。
     *   可以用IntDef替代Enum  更省内存 轻量级
     */
    //枚举
    @IntDef({TAP, OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventCode {}


    public @Events.EventCode int code;
    public T content;

    public static <O> Events<O> setContent(O t) {
        Events<O> events = new Events<>();
        events.content = t;
        return events;
    }

    public <T> T getContent() {
        return (T) content;
    }

}