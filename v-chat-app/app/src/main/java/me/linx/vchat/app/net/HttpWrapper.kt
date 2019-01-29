package me.linx.vchat.app.net

import com.blankj.utilcode.util.AppUtils
import me.linx.vchat.app.constant.AppConfigs
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpWrapper {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(AppConfigs.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(AppConfigs.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(AppConfigs.writeTimeout, TimeUnit.MILLISECONDS)
            .addInterceptor()
            .build()
    }

    private fun OkHttpClient.Builder.addInterceptor() = apply {
        if (AppUtils.isAppDebug()) addInterceptor(HttpLogInterceptor())
    }

}