package com.example.hello.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var instance: Retrofit? = null
    private const val CONNECT_TIMEOUT_SEC = 20000L

    fun getInstance() : Retrofit {
        if(instance == null){

            // 로깅인터셉터 세팅
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//
//            // OKHttpClient에 로깅인터셉터 등록
//            val client = OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
//                .build()

            instance = Retrofit.Builder()
                .baseUrl("http://192.168.0.77:8080")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client) // Retrofit 객체에 OkHttpClient 적용
                .build()
        }
        return instance!!
    }
}

//class MyApp: Application() {
//    private var instance: MyApp? = null
//
//    override fun onCreate() {
//        instance = this
//        super.onCreate()
//    }
//
//    fun getInstance(): MyApp? {
//        return instance
//    }
//
//    fun getContext(): Context? {
//        return instance
//    }
//}
//
//class UserAuth: Interceptor{
//    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
//        val  requestBuilder = chain.request().newBuilder()
//        Utils.init(MyApp().getContext()!!)
//
//        val authorizationHeader: String? = Utils.getAccessToken("none")
//
//        return chain.proceed(requestBuilder.header("Authorization", "Bearer $authorizationHeader").build())
//    }
//}