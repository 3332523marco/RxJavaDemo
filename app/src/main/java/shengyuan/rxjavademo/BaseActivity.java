package shengyuan.rxjavademo;

import android.app.Activity;
import android.os.Bundle;

import shengyuan.rxjavademo.component.MainAppComponent;


/**
 * Created by Marco on 17/2/14.
 */
public abstract  class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent(MainAppliaction.get(this).getMainAppComponent());
    }

    protected abstract  void setupActivityComponent(MainAppComponent mainAppComponent);
}
