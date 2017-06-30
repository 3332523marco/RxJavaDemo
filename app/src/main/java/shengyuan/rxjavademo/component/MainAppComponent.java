package shengyuan.rxjavademo.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import shengyuan.rxjavademo.MainActivity;
import shengyuan.rxjavademo.SecondActivity;
import shengyuan.rxjavademo.data.Student;
import shengyuan.rxjavademo.module.MainAppModule;
import shengyuan.rxjavademo.module.NetServiceModule;
import shengyuan.rxjavademo.net.NetApiService;
import shengyuan.rxjavademo.scope.PoetryQualifier;


/**
 * Created by Marco on 17/2/14.
 */
@Singleton
@Component(modules = {MainAppModule.class,NetServiceModule.class})
public interface MainAppComponent {
    Application getApplication();


    MainActivity inject(MainActivity mainActivity);//Dagger2会从目标类开始查找@Inject注解，自动生成依赖注入的代码，调用inject可完成依赖的注入。
    SecondActivity inject(SecondActivity secondActivity);//Dagger2会从目标类开始查找@Inject注解，自动生成依赖注入的代码，调用inject可完成依赖的注入。

    NetApiService getNetApiService();

    @PoetryQualifier("A") // @Named是Dagger2对于@Qualifier一个默认实现，我们也可以自定义
    Student getStudent1();

    @PoetryQualifier("B")
    Student getStudent2();

    @PoetryQualifier("C")
    Student getStudent3();

    @PoetryQualifier("D")
    Student getStudent4();
}
