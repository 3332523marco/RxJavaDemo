package shengyuan.rxjavademo.module;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import shengyuan.rxjavademo.data.Student;
import shengyuan.rxjavademo.scope.PoetryQualifier;


/**
 * Created by Marco on 17/2/14.
 */
@Module
public class MainAppModule {
    private Application application;

    public MainAppModule(Application application){
        this.application=application;
    }

    @Provides
    @Singleton
    public Application provideApplication(){
        return application;
    }

    @Provides
    List<Student> provideList() {
        List<Student> list = new ArrayList<>();
        list.add(provideStudent1());
        list.add(provideStudent2());
        list.add(provideStudent3());
        return list;
    }

    @PoetryQualifier("A")
    @Provides
    Student provideStudent1() {
        return new Student("test1");
    }

    @PoetryQualifier("B")
    @Provides
    Student provideStudent2() {
        return new Student("test2");
    }

    @PoetryQualifier("C")
    @Provides
    Student provideStudent3() {
        return new Student("test3");
    }
}
