package com.nsoni.flickr;

import android.content.Context;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {


    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            try {
                return chain.proceed(chain.request().newBuilder()
                        .header("Cache-Control", "public, max-age=" + 5).build());
            } catch (IOException e) {
                Request offlineRequest = chain.request().newBuilder()
                        .header("Cache-Control", "public, only-if-cached," +
                                "max-stale=" + 60 * 60 * 24 /* tolerate 1 day stale*/)
                        .build();
                return chain.proceed(offlineRequest);
            }
        }
    };
    private static ServiceGenerator serviceGeneratorInstance;
    private final String BASE_URL = "https://api.flickr.com/";
    private Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
    private long cacheSize = 5 * 1024 * 1024L;
    private Cache cache;
    private OkHttpClient httpClient;
    private Retrofit retrofit;


    private ServiceGenerator(Context context) {
        cache = new Cache(context.getCacheDir(), cacheSize);
        httpClient = new OkHttpClient.Builder().cache(cache)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .build();
        retrofit = builder
                .client(httpClient)
                .build();
    }

    public static ServiceGenerator getInstance(Context context) {
        if (serviceGeneratorInstance == null) {
            synchronized (ServiceGenerator.class) {
                if (serviceGeneratorInstance == null) {
                    serviceGeneratorInstance = new ServiceGenerator(context);
                }
            }
        }
        return serviceGeneratorInstance;
    }


    public <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
