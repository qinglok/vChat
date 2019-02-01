package me.linx.vchat.app.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import me.linx.vchat.app.utils.launch
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
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
            it.start()
        }.let { callback ->
            var throwable: Throwable? = null

            val asyncData: Deferred<T?> = GlobalScope.async {
                var response: Response? = null

                try {
                    response = HttpWrapper.okHttpClient.newCall(this@call).execute()
                } catch (t: Throwable) {
                    throwable = t
                }

                if (throwable == null && response != null) {
                    if (response.isSuccessful) {
                        Gson().fromJson<T>(response.body()?.charStream(), object : TypeToken<T>() {}.type)
                    } else {
                        throwable = IOException("request to ${url()} is fail; http code: ${response.code()}!")
                        null
                    }
                } else {
                    null
                }
            }

            asyncData.launch(Dispatchers.Main) {
                callback.finish()

                it?.let { t ->
                    callback.success(t)
                }

                throwable?.let { t ->
                    callback.error(t)
                }
            }
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