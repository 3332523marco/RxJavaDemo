package shengyuan.rxjavademo;

import android.app.Application;
import android.content.Context;

import shengyuan.rxjavademo.component.DaggerMainAppComponent;
import shengyuan.rxjavademo.component.MainAppComponent;
import shengyuan.rxjavademo.module.MainAppModule;
import shengyuan.rxjavademo.module.NetServiceModule;

/**
 * Created by Marco on 17/2/14.
 */
public class MainAppliaction extends Application {

    private MainAppComponent mainAppComponent;

    public static MainAppliaction get(Context context) {
        return (MainAppliaction) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Global dependencies graph is created here
        mainAppComponent = DaggerMainAppComponent.builder()
                .mainAppModule(new MainAppModule(this))
                .netServiceModule(new NetServiceModule())
//                .mainAppMoudle(new MainAppModule())
//                .appModule(new AppModule(this))
                .build();
    }

    //Just a helper to provide appComponent to local components which depend on it
    public MainAppComponent getMainAppComponent() {
        return mainAppComponent;
    }



}