package me.linx.vchat.app.net

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.constant.AppConfigs
import okhttp3.Call
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpWrapper {
    val httpTasks by lazy { hashMapOf<Int, ArrayList<HttpTask>>() }
    val requestCache by lazy { hashMapOf<Any, Call>() }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(AppConfigs.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(AppConfigs.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(AppConfigs.writeTimeout, TimeUnit.MILLISECONDS)
//            .sslSocketFactory()
            .addInterceptor()
            .build()
    }

    val okHttpGlideClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
//            .sslSocketFactory()
            .build()
    }

    private fun OkHttpClient.Builder.sslSocketFactory() = apply {
        Utils.getApp()?.assets?.open("vchat.cer")?.let {
            with(HttpsUtils.getSslSocketFactory(it)) {
                sslSocketFactory(sSLSocketFactory, trustManager)
            }
        }
    }

    private fun OkHttpClient.Builder.addInterceptor() = apply {
        if (AppUtils.isAppDebug()) addInterceptor(HttpLogInterceptor())
    }

    fun addHttpTask(code: Int, task: HttpTask) {
        httpTasks[code].apply {
            if (this == null) {
                httpTasks[code] = arrayListOf<HttpTask>().apply {
                    add(task)
                }
            } else {
                add(task)
            }
        }
    }

    fun cancel(tag: Any) {
        GlobalScope.launch {
            requestCache[tag]?.cancel()
            requestCache.remove(tag)
        }
    }

    fun cancelAll() {
        GlobalScope.launch {
            requestCache.keys.iterator().also {
                while (it.hasNext()) {
                    HttpWrapper.cancel(it.next())
                }
            }
        }
    }

}