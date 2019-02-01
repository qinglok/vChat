package me.linx.vchat.app.net

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import me.linx.vchat.app.App
import me.linx.vchat.app.constant.AppConfigs
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpWrapper {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(AppConfigs.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(AppConfigs.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(AppConfigs.writeTimeout, TimeUnit.MILLISECONDS)
            .sslSocketFactory()
            .addInterceptor()
            .build()
    }

    val okHttpGlideClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .sslSocketFactory()
            .build()
    }

    private fun OkHttpClient.Builder.sslSocketFactory() = apply {
        Utils.getApp().assets.open("vchat.cer").let {
            sslSocketFactory(HttpsOnlySelfUtils.getSSLSocketFactory(it), HttpsOnlySelfUtils.getX509TrustManager())
        }
    }

    private fun OkHttpClient.Builder.addInterceptor() = apply {
        if (AppUtils.isAppDebug()) addInterceptor(HttpLogInterceptor())
    }

}