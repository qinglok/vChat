package me.linx.vchat.app.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import me.linx.vchat.app.utils.then
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.URLConnection.getFileNameMap

fun String.http(tag: Any = this, withOutBaseUrl: Boolean = false) = RequestWrapper(tag, this, withOutBaseUrl)

inline fun <reified T> RequestWrapper.get(crossinline init: HttpHandler<T>.() -> Unit) = call(buildGetRequest(), init)

inline fun <reified T> RequestWrapper.post(crossinline init: HttpHandler<T>.() -> Unit) = call(buildPostRequest(), init)

inline fun <reified T> RequestWrapper.call(request: Request, crossinline init: HttpHandler<T>.() -> Unit) {
    GlobalScope.launch {
        with(HttpHandler<T>()) {
            init.invoke(this)

            setTag(tag)

            launch(Dispatchers.Main) {
                onStart()
            }

            if (withLoader) {
                launch(Dispatchers.Main){
                    showLoader()
                }
            }

            var throwable: Throwable? = null

            val asyncData: Deferred<T?> = async {
                var response: Response? = null

                try {
                    HttpWrapper.okHttpClient.newCall(request).also {
                        HttpWrapper.requestCache[tag] = it
                        response = it.execute()
                    }
                } catch (t: Throwable) {
                    throwable = t
                }

                if (throwable == null && response != null) {
                    if (response!!.isSuccessful) {
                        val t: T? = try {
                            Gson().fromJson<T>(response!!.body()?.charStream(), object : TypeToken<T>() {}.type)
                        } catch (e: Throwable) {
                            throwable = e
                            null
                        }

                        if (t != null && t is JsonResult<*> && HttpWrapper.httpTasks.containsKey(t.code)) {
                            HttpWrapper.httpTasks[t.code]?.forEach { task ->
                                task.handle()
                            }
                            HttpWrapper.requestCache.remove(tag)
                            null
                        } else {
                            HttpWrapper.requestCache.remove(tag)
                            t
                        }
                    } else {
                        throwable = IOException("request to ${request.url()} is fail; http code: ${response!!.code()}!")
                        HttpWrapper.requestCache.remove(tag)
                        null
                    }
                } else {
                    HttpWrapper.requestCache.remove(tag)
                    null
                }
            }

            asyncData.then(Dispatchers.Main) {
                if (withLoader) {
                    hideLoader()
                }

                onFinish()

                if (isCancel) {
                    onCancel()
                } else {
                    it?.let { t ->
                        onSuccess(t)
                    }

                    throwable?.let { t ->
                        onError(t)
                    }
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