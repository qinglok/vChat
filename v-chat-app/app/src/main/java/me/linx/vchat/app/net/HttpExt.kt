package me.linx.vchat.app.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.net.URLConnection.getFileNameMap

@Suppress("ObjectPropertyName")
const val _OK = 0

fun String.http(withOutBaseUrl : Boolean = false) = RequestWrapper(this, withOutBaseUrl)

inline fun <reified T> RequestWrapper.get(init: HttpCallback<T>.() -> Unit) = buildGetRequest().call(init)

inline fun <reified T> RequestWrapper.post(init: HttpCallback<T>.() -> Unit) = buildPostRequest().call(init)

inline fun <reified T> Request.call(init: HttpCallback<T>.() -> Unit): Unit =
    HttpCallback<T>()
        .also {
            init.invoke(it)
            it.start()
        }.let { callback ->
            GlobalScope.launch {
                withContext(Dispatchers.Default) {
                    HttpWrapper.okHttpClient.newCall(this@call).execute()
                }.also {
                    launch(Dispatchers.Main) {
                        callback.finish()
                        if (it.isSuccessful) {
                            it.body()?.charStream()?.let { reader ->
                                callback.success(Gson().fromJson<T>(reader, object : TypeToken<T>() {}.type))
                            }
                        } else {
                            callback.error(IOException("request to ${url()} is fail; http code: ${it.code()}!"))
                        }
                    }
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