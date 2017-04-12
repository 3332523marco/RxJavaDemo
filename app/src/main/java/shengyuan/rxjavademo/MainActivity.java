package shengyuan.rxjavademo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import shengyuan.rxjavademo.component.MainAppComponent;
import shengyuan.rxjavademo.data.EventCenter;
import shengyuan.rxjavademo.data.HttpResult;
import shengyuan.rxjavademo.data.Student;
import shengyuan.rxjavademo.data.Subject;
import shengyuan.rxjavademo.net.NetApiService;
import shengyuan.rxjavademo.scope.PoetryQualifier;
import shengyuan.rxjavademo.subscribers.HttpSubscribersUtils;
import shengyuan.rxjavademo.subscribers.ProgressSubscriber;
import shengyuan.rxjavademo.subscribers.SubscriberOnNextListener;
import shengyuan.rxjavademo.util.Constants;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.testMap)
    Button testMap;
    @Bind(R.id.testFilter)
    Button testFilter;
    @Bind(R.id.testThread)
    Button testThread;
    @Bind(R.id.testRetrofit)
    Button testRetrofit;
    @Bind(R.id.testDefer)
    Button testDefer;

    @PoetryQualifier("A")
    @Inject
    Student mStudent1;
    @PoetryQualifier("B")
    @Inject
    Student mStudent2;
    @PoetryQualifier("C")
    @Inject
    Student mStudent3;
    @Inject
    List<Student> mList;
    @Inject
    NetApiService netApiService;

    @Override
    protected void setupActivityComponent(MainAppComponent mainAppComponent) {
        mainAppComponent
                .inject(this);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
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
        switch (eventCenter.getEventCode()) {
            case Constants.EVENT_BUS.EVENT_BUS_TYPE_MAIN:
                Log.i(TAG, "rxbus from SecondActivity: " + (String)eventCenter.getData());
                break;
        }
    }

    @OnClick({R.id.testMap, R.id.testFilter, R.id.testThread, R.id.testRetrofit,R.id.testDefer,R.id.testRxBus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.testMap:
                testMap();
                break;
            case R.id.testFilter:
                testFilter();
                break;
            case R.id.testThread:
                testThread();
                break;
            case R.id.testDefer:
                testDefer();
                break;
            case R.id.testRetrofit:
                loadNetService(0, 10);
                break;
            case R.id.testRxBus:
                startActivity(new Intent(mContext,SecondActivity.class));
                break;
        }
    }

    /**
     * rxlifecycle 框架的使用
     *
     * 让你的activity继承RxActivity,RxAppCompatActivity,RxFragmentActivity
     * 让你的fragment继承RxFragment,RxDialogFragment;
     *
     * bindToLifecycle 方法
     * 在子类使用Observable中的compose操作符，调用，完成Observable发布的事件和当前的组件绑定，实现生命周期同步。从而实现当前组件生命周期结束时，自动取消对Observable订阅。
     *
     * .compose(this.<Long>bindUntilEvent(ActivityEvent.STOP ))   //当Activity执行Onstop()方法是解除订阅关系
     * bindUntilEvent( ActivityEvent event)
     * ActivityEvent.CREATE: 在Activity的onCreate()方法执行后，解除绑定。
     * ActivityEvent.START:在Activity的onStart()方法执行后，解除绑定。
     * ActivityEvent.RESUME:在Activity的onResume()方法执行后，解除绑定。
     * ActivityEvent.PAUSE: 在Activity的onPause()方法执行后，解除绑定。
     * ActivityEvent.STOP:在Activity的onStop()方法执行后，解除绑定。
     * ActivityEvent.DESTROY:在Activity的onDestroy()方法执行后，解除绑定。
     * 本实例中统一加了bindToLifecycle 来统一绑定其当前activity的生命周期
     *
     */

    private void testMap() {
        //flatMap操作符的运行结果
        /**
         * flatMap操作符是把Observable产生的结果转换成多个Observable，然后把这多个Observable“扁平化”成一个Observable，并依次提交产生的结果给订阅者。
         flatMap操作符通过传入一个函数作为参数转换源Observable，在这个函数中，你可以自定义转换规则，最后在这个函数中返回一个新的Observable，然后flatMap操作符通过合并这些Observable结果成一个Observable，并依次提交结果给订阅者。
         值得注意的是，flatMap操作符在合并Observable结果时，有可能存在交叉的情况
         */
        Observable.just(10, 20, 30).compose(this.<Integer>bindToLifecycle()).flatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                //10的延迟执行时间为200毫秒、20和30的延迟执行时间为180毫秒
                int delay = 200;
                if (integer > 10)
                    delay = 180;

                return Observable.from(new Integer[]{integer, integer / 2}).delay(delay, TimeUnit.MILLISECONDS);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "flatMap Next: " + integer);
            }
        })  //这个订阅关系跟Activity绑定，Observable 和activity生命周期同步
        ;

        //concatMap操作符的运行结果
        /**
         * cancatMap操作符与flatMap操作符类似，都是把Observable产生的结果转换成多个Observable，然后把这多个Observable“扁平化”成一个Observable，并依次提交产生的结果给订阅者。
         与flatMap操作符不同的是，concatMap操作符在处理产生的Observable时，采用的是“连接(concat)”的方式，而不是“合并(merge)”的方式，这就能保证产生结果的顺序性，也就是说提交给订阅者的结果是按照顺序提交的，不会存在交叉的情况。
         */
        Observable.just(10, 20, 30).compose(this.<Integer>bindToLifecycle()).concatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                //10的延迟执行时间为200毫秒、20和30的延迟执行时间为180毫秒
                int delay = 200;
                if (integer > 10)
                    delay = 180;

                return Observable.from(new Integer[]{integer, integer / 2}).delay(delay, TimeUnit.MILLISECONDS);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "concatMap Next: " + integer);
            }
        });

        //switchMap操作符的运行结果
        /**
         * switchMap操作符会保存最新的Observable产生的结果而舍弃旧的结果
         */
        Observable.just(10, 20, 30).compose(this.<Integer>bindToLifecycle()).switchMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                //10的延迟执行时间为200毫秒、20和30的延迟执行时间为180毫秒
                int delay = 200;
                if (integer > 10)
                    delay = 180;

                return Observable.from(new Integer[]{integer, integer / 2}).delay(delay, TimeUnit.MILLISECONDS);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "switchMap Next: " + integer);
            }
        });
        Observable.just(mStudent1, mStudent2, mStudent3).compose(this.<Student>bindToLifecycle())
                //使用map进行转换，参数1：转换前的类型，参数2：转换后的类型
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student i) {
                        String name = i.name;//获取Student对象中的name
                        return name;//返回name
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, "name " + s);
                    }
                });

        /**
         * map 是把发射对象转成另外一个对象发射出去
         * flatMap 是把发射对象转成另外一个Observable,进而把这个Observable发射的对象发射出去
         */
        Observable.create(new Observable.OnSubscribe<List<Student>>() {
            @Override
            public void call(Subscriber<? super List<Student>> subscriber) {
                subscriber.onNext(mList);
            }
        }).compose(this.<List<Student>>bindToLifecycle()).flatMap(new Func1<List<Student>, Observable<Student>>() {
            @Override
            public Observable<Student> call(List<Student> users) {
                return Observable.from(users);
            }
        }).filter(new Func1<Student, Boolean>() {
            @Override
            public Boolean call(Student user) {
                return user.name.equals("test3");
            }
        }).subscribe(new Action1<Student>() {
            @Override
            public void call(Student user) {
                Log.i(TAG, "Observable.create.subscribe " + user.name);
            }
        });
    }

    private void testFilter() {
        /**
         * filter操作符是对源Observable产生的结果按照指定条件进行过滤，只有满足条件的结果才会提交给订阅者
         */
        /**
         * from和just的区别：
         * from会依次返回list的每个item，而just会直接把list返回相当于输入什么返回什么。
         */
        Observable.just(mStudent1, mStudent2, mStudent3).compose(this.<Student>bindToLifecycle())
                //使用map进行转换，参数1：转换前的类型，参数2：转换后的类型
                .filter(new Func1<Student, Boolean>() {

                    @Override
                    public Boolean call(Student student) {
                        return student.name.startsWith("test2");
                    }
                })
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student s) {
                        Log.i(TAG, "Observable.just " + s.name);
                    }
                });

        Observable.from(mList).compose(this.<Student>bindToLifecycle())
                .filter(new Func1<Student, Boolean>() {
                    @Override
                    public Boolean call(Student student) {
                        return student.name.startsWith("test2");
                    }
                })
                .subscribe(new Observer<Student>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "Observable.from onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "Observable.from onError");

                    }

                    @Override
                    public void onNext(Student student) {
                        Log.i(TAG, "Observable.from onNext " + student.name);

                    }
                });
    }

    private void testThread() {
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Subscription subscription = Observable.create(new Observable.OnSubscribe<BluetoothDevice>() {
            @Override
            public void call(final Subscriber<? super BluetoothDevice> subscriber) {
                final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                        // 判断是否还在订阅，避免发送不必要的数据
                        Log.i(TAG, "onLeScan " + i + " " + Thread.currentThread().getName());
                        if (!subscriber.isUnsubscribed()) {
                            if (i == -99 || i == -88) {
                                //取消订阅
                                subscriber.unsubscribe();
                            }
                            subscriber.onNext(bluetoothDevice);
                        }
                    }
                };
                mBluetoothAdapter.startLeScan(leScanCallback);
                // 使用 Subscriptions 的 create 方法创建一个只有取消订阅时才调用的方法
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "onLeScan stopLeScan ");
                        mBluetoothAdapter.stopLeScan(leScanCallback);
                    }
                }));
            }
            /**
             * subscribeOn(): 是指subscribe()所发生的线程，即Observable.OnSubscibe被激活时所处的线程，或者事件产生的的线程
             observeOn() 指定Subscriber所运行的线程，或者叫做事件消费的线程
             */
        }).compose(this.<BluetoothDevice>bindToLifecycle()).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread()).subscribe(new Subscriber<BluetoothDevice>() {
                    /**
                     * AndroidSchedulers.mainThread() 可设为在主线程
                     *
                     * RxJava 内置的 Scheduler 有：
                     immediate 同步执行
                     trampoline 把任务放到当前线程的队列中，等当前任务执行完了，再继续执行队列中的任务
                     newThread 对于每个任务创建一个新的线程去执行
                     computation 计算线程，用于需要大量 CPU 计算的任务
                     io 用于执行 io 操作的任务
                     test 用于测试和调试
                     */
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onLeScan onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onLeScan onError");

                    }

                    @Override
                    public void onNext(BluetoothDevice bluetoothDevice) {
                        Log.i(TAG, "onLeScan onNext " + Thread.currentThread().getName());

                    }
                });
    }

    int value;

    private void testDefer(){
        /**
         * 直到有订阅，才会创建Observable
         * 具有延时的效果。
         *
         * just()，from()这类能够创建Observable的操作符在创建之初，就已经存储了对象的值，而不被订阅的时候。
         *
         * 使用defer()操作符的唯一缺点就是，每次订阅都会创建一个新的Observable对象。
         * create()操作符则为每一个订阅者都使用同一个函数，所以，后者效率更高。一如既往地，如果有必要可以亲测性能或者尝试优化。
         */

        value = 10;
        Observable<String> o1 = Observable.just("just result: " + value).compose(this.<String>bindToLifecycle());
        value = 12;
        o1.subscribe(new Action1<String>() {

            @Override
            public void call(String t) {
                Log.i(TAG, "testDefer just call "+t);
            }
        });

        value = 12;
        Observable<String> o2 =
                Observable.defer(new Func0<Observable<String>>() {

                    @Override
                    public Observable<String> call() {
                        return Observable.just("defer result: " + value);
                    }
                }).compose(this.<String>bindToLifecycle());
        value = 20;

        o2.subscribe(new Action1<String>() {

            @Override
            public void call(String t) {
                Log.i(TAG, "testDefer call "+t);
            }
        });

    }

    private void loadNetService(int start, int count) {
        HttpSubscribersUtils.getInstance().getTopMovieData(netApiService.getTopMovie(start, count).compose(this.<HttpResult<List<Subject>>>bindToLifecycle()), new ProgressSubscriber(new SubscriberOnNextListener<List<Subject>>() {
            @Override

            public void onNext(List<Subject> subjects) {
                Log.i(TAG, "loadNetService onNext " + subjects.toString());
            }
        }, MainActivity.this));
    }
}
