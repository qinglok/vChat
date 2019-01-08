package me.linx.vchat.app.net

import me.linx.vchat.app.constant.AppConfigs
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Net {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(AppConfigs.netConnectTimeOut, TimeUnit.SECONDS)
        .build()

    val retrofitClient = Retrofit.Builder()
        .baseUrl(AppConfigs.netBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    val service = retrofitClient.create(NetApi::class.java)
}