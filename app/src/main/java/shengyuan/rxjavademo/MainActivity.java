package shengyuan.rxjavademo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import shengyuan.rxjavademo.component.MainAppComponent;
import shengyuan.rxjavademo.data.Student;
import shengyuan.rxjavademo.data.Subject;
import shengyuan.rxjavademo.net.NetApiService;
import shengyuan.rxjavademo.scope.PoetryQualifier;
import shengyuan.rxjavademo.subscribers.HttpSubscribersUtils;
import shengyuan.rxjavademo.subscribers.ProgressSubscriber;
import shengyuan.rxjavademo.subscribers.SubscriberOnNextListener;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void setupActivityComponent(MainAppComponent mainAppComponent) {
        mainAppComponent
                .inject(this);
    }

    @OnClick({R.id.testMap, R.id.testFilter, R.id.testThread,R.id.testRetrofit})
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
            case R.id.testRetrofit:
                loadNetService(0, 10);
                break;
        }
    }

    private void testMap() {

        Observable.just(mStudent1, mStudent2, mStudent3)
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
        }).flatMap(new Func1<List<Student>, Observable<Student>>() {
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
        Observable.just(mStudent1, mStudent2, mStudent3)
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

        Observable.from(mList)
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
        }).subscribeOn(Schedulers.io())
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

    public void loadNetService(int start, int count) {
        HttpSubscribersUtils.getInstance().getTopMovieData(netApiService.getTopMovie(start, count),new ProgressSubscriber(new SubscriberOnNextListener<List<Subject>>() {
            @Override
            public void onNext(List<Subject> subjects) {
                Log.i(TAG, "loadNetService onNext " + subjects.toString());
            }
        }, MainActivity.this));
    }

}
