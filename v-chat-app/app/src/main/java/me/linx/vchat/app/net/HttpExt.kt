package me.linx.vchat.app.net

import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import me.linx.vchat.app.utils.transfor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.lang.Exception
import java.net.URLConnection.getFileNameMap

@Suppress("ObjectPropertyName")
const val _OK = 0

fun String.http(withOutBaseUrl: Boolean = false) = RequestWrapper(this, withOutBaseUrl)

inline fun <reified T> RequestWrapper.get(init: HttpCallback<T>.() -> Unit) = buildGetRequest().call(init)

inline fun <reified T> RequestWrapper.post(init: HttpCallback<T>.() -> Unit) = buildPostRequest().call(init)

inline fun <reified T> Request.call(init: HttpCallback<T>.() -> Unit): Unit =
    HttpCallback<T>()
        .also {
            init.invoke(it)
        }.let { callback ->
            Single.create<Response> { e ->
                try {
                    e.onSuccess(HttpWrapper.okHttpClient.newCall(this@call).execute())
                } catch (t: Throwable) {
                    e.onError(t)
                }
            }.transfor().subscribe(object : DisposableSingleObserver<Response>() {
                override fun onStart() {
                    callback.start()
                }

                override fun onSuccess(t: Response) {
                    callback.finish()
                    try {
                        Gson().fromJson<T>(t.body()?.charStream(), object : TypeToken<T>() {}.type)
                    } catch (e: Exception) {
                        callback.error(e)
                        LogUtils.d(e)
                        null
                    }finally {
                        dispose()
                    }?.let {
                        callback.success(it)
                    }
                }

                override fun onError(e: Throwable) {
                    callback.finish()
                    callback.error(e)
                    dispose()
                    LogUtils.d(e)
                }
            })
        }

fun File.createRequestBody(): RequestBody = RequestBody.create(MediaType.parse(mediaType()), this)

fun File.mediaType() = getFileNameMap().getContentTypeFor(name) ?: when (extension.toLowerCase()) {
    "jpg" -> "image/jpeg"
    "png" -> "image/png"
    "gif" -> "image/gif"
    "xml" -> "application/xml"
    "json" -> "application/json"
    "js" -> "application/javascript"
    "apk" -> "application/vnd.android.package-archive"
    "md" -> "text/x-markdown"
    "webp" -> "image/webp"
    else -> "application/octet-stream"
}