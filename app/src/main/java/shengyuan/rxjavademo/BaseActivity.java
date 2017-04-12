package shengyuan.rxjavademo;

import android.content.Context;
import android.os.Bundle;

import com.trello.rxlifecycle.components.RxActivity;

import butterknife.ButterKnife;
import rx.functions.Action1;
import shengyuan.rxjavademo.component.MainAppComponent;
import shengyuan.rxjavademo.data.EventCenter;
import shengyuan.rxjavademo.rxbus.Events;
import shengyuan.rxjavademo.rxbus.RxBus;


/**
 * Created by Marco on 17/2/14.
 */
public abstract  class BaseActivity extends RxActivity {


    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        mContext = this;
        setupActivityComponent(MainAppliaction.get(this).getMainAppComponent());
        if(isBindRxBusHere()){
            registerRxBus(this);
        }
        initViewsAndEvents();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    protected abstract  void setupActivityComponent(MainAppComponent mainAppComponent);

    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * init all views and add events
     */
    protected abstract void initViewsAndEvents();

    /**
     * is bind rxBus
     *
     * @return
     */
    protected abstract boolean isBindRxBusHere();

    /**
     * when event coming
     *
     * @param eventCenter
     */
    protected abstract void onEventComing(EventCenter eventCenter);

    protected void sendEvent(EventCenter eventCenter){
        if(eventCenter!=null)
        RxBus.getInstance().send(Events.OTHER, eventCenter);
    }


    private void registerRxBus(RxActivity activity){
        RxBus.with(activity)
                .setEvent(Events.OTHER)
                .onNext(new Action1<Events<?>>() {
                    @Override
                    public void call(Events<?> events) {
                        EventCenter event = events.getContent();
                        onEventComing(event);
                    }
                })
                .onError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }) // 异常处理，默认捕获异常，不做处理，程序不会crash。
                .create();
    }
}
