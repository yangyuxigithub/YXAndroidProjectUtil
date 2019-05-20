package com.yangyuxi.yx_projectutil.yx_http;

import com.yangyuxi.yx_projectutil.yx_log.YXLog;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class IHttp {

    private Retrofit retrofit;

    // 单例模式
    private static IHttp instance;

    private IHttp() {

    }

    public static synchronized IHttp getInstance() {
        if (instance == null) instance = new IHttp();
        return instance;
    }

    /*
     * 配置 Retrofit
     * */
    public void setRetrofit(String baseUrl) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // get retrofit
    public Retrofit getRetrofit() {
        return retrofit;
    }


    private static class LogInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            YXLog.d(IHttp.class, "okhttp3:" + request.toString());//输出请求前整个url
            long t1 = System.nanoTime();
            okhttp3.Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            YXLog.d(IHttp.class, t1 + "----------" + t2);
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            YXLog.e(IHttp.class, "response body:" + content);//输出返回信息
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    }


}
