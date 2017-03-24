package shengyuan.rxjavademo.net;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import shengyuan.rxjavademo.data.HttpResult;
import shengyuan.rxjavademo.data.Subject;

/**
 * Created by Marco on 17/2/20.
 */
public interface NetApiService {

    @GET("/top250")
    Observable<HttpResult<List<Subject>>> getTopMovie(@Query("start") int start, @Query("count") int count);

//    @GET("/repos/{owner}/{repo}/contributors")
//    void contributors(@Path("owner") String owner, @Path("repo") String repo, Callback<List<Contributions>> callback);

//    @Path：所有在网址中的参数（URL的问号前面），如：
//    https://api.github.com/repos/{owner}/{repo}/contributors
//    @Query：URL问号后面的参数，如：
//    https://api.github.com/repos/square/retrofit/contributors?access_token={access_token}
//    @QueryMap：相当于多个@Query
//    @Field：用于POST请求，提交单个数据
//    @Body：相当于多个@Field，以对象的形式提交
}
