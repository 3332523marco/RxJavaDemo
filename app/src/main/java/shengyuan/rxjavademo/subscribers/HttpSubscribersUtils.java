package shengyuan.rxjavademo.subscribers;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import shengyuan.rxjavademo.data.ApiException;
import shengyuan.rxjavademo.data.HttpResult;
import shengyuan.rxjavademo.data.Subject;

/**
 * Created by Marco on 17/3/24.
 */
public class HttpSubscribersUtils {

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final HttpSubscribersUtils INSTANCE = new HttpSubscribersUtils();
    }

    //获取单例
    public static HttpSubscribersUtils getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     * 用于获取豆瓣电影Top250的数据
     * @param subscriber  由调用者传过来的观察者对象
     */
    public void getTopMovieData(Observable<HttpResult<List<Subject>>> observable,Subscriber<List<Subject>> subscriber){
        Observable observable2 = observable
                .map(new HttpResultFunc<List<Subject>>())
                .retryWhen(new RetryWithDelay(3, 10000)); //总共重试3次，重试间隔3000毫秒   遇到onError则会重试   而repeatWhen是当事件完成后重试  即onComplete
        toSubscribe(observable2, subscriber);
    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T>   Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            if (httpResult.getCount() == 0) {
                throw new ApiException(100);
            }
            return httpResult.getSubjects();
        }
    }
}
