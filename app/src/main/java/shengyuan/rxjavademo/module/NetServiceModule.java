package shengyuan.rxjavademo.module;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import shengyuan.rxjavademo.net.NetApiService;


/**
 * Created by Marco on 17/2/15.
 */
@Module
public class NetServiceModule {
    private static final String API_URL = "https://api.douban.com/v2/movie/";


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        return okHttpClient;
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(Application application, OkHttpClient okHttpClient) {
        //通过RestAdapter生成一个刚才定义的接口的实现类，使用的是动态代理。
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setClient(new OkClient(okHttpClient))
                .setEndpoint(API_URL);
        return builder.build();
    }

    @Provides
    @Singleton
    NetApiService provideNetApiService(RestAdapter restAdapter) {
        return restAdapter.create(NetApiService.class);
    }
}
