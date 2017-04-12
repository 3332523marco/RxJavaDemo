package shengyuan.rxjavademo;

import butterknife.OnClick;
import shengyuan.rxjavademo.component.MainAppComponent;
import shengyuan.rxjavademo.data.EventCenter;
import shengyuan.rxjavademo.util.Constants;

/**
 * Created by Marco on 17/4/12.
 */
public class SecondActivity extends BaseActivity {


    @Override
    protected void setupActivityComponent(MainAppComponent mainAppComponent) {
        mainAppComponent
                .inject(this);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_second;
    }

    @Override
    protected void initViewsAndEvents() {

    }

    @Override
    protected boolean isBindRxBusHere() {
        return true;
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {

    }

    @OnClick(R.id.testRxBus)
    public void onClick() {
        sendEvent(new EventCenter<>(Constants.EVENT_BUS.EVENT_BUS_TYPE_MAIN, "I'm Second"));
    }
}
