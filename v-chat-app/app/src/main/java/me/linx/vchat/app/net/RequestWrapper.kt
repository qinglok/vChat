package me.linx.vchat.app.net

import me.linx.vchat.app.data.api.Api
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.File


class RequestWrapper(val tag : Any, url: String, withOutBaseUrl : Boolean) {
    private val headers = arrayListOf<Pair<String, String>>()
    private val params = arrayListOf<Pair<String, Any>>()
    private val fullUrl = if (withOutBaseUrl)
        url
    else{
        Api.baseUrl + url
    }

    fun headers(vararg headers: Pair<String, String?>) = apply {
        headers.forEach {
            it.second?.let { second ->
                this.headers.add(Pair(it.first, second))
            }
        }
    }

    @Suppress("unused")
    fun headers(map: Map<String, String?>) = apply {
        map.forEach {
            it.value?.let { value ->
                this.headers.add(Pair(it.key, value))
            }
        }
    }

    fun params(vararg params: Pair<String, Any?>) = apply {
        params.forEach {
            it.second?.let { second ->
                this.params.add(Pair(it.first, second))
            }
        }
    }

    @Suppress("unused")
    fun params(map: Map<String, Any?>) = apply {
        map.forEach {
            it.value?.let { value ->
                this.params.add(Pair(it.key, value))
            }
        }
    }

    fun buildGetRequest(): Request =
        Request.Builder()
            .tag(tag)
            .buildHeader()
            .buildGetUrl()
            .build()

    fun buildPostRequest(): Request =
        Request.Builder()
            .tag(tag)
            .buildHeader()
            .buildPostUrl()
            .buildBody()
            .build()

    private fun Request.Builder.buildHeader() = apply {
        headers.forEach {
            header(it.first, it.second)
        }
    }

    private fun Request.Builder.buildGetUrl() = apply {
        HttpUrl.parse(fullUrl)?.newBuilder()?.apply {
            params.forEach {
                addQueryParameter(it.first, it.second.toString())
            }
        }?.let {
            url(it.build())
        }
    }


    private fun Request.Builder.buildPostUrl() =  apply {
        HttpUrl.parse(fullUrl)?.let {
            url(it)
        }
    }

    private fun Request.Builder.buildBody() = apply {
        if (isMultiPart()) {
            MultipartBody.Builder().apply {
                params.forEach {
                    val key = it.first
                    val value = it.second

                    if (value is File) {
                        addFormDataPart(
                            key,
                            value.name,
                            value.createRequestBody()
                        )
                    } else {
                        addFormDataPart(key, value.toString())
                    }
                }
            }.let {
                post(it.build())
            }
        } else {
            FormBody.Builder().apply {
                params.forEach { add(it.first, it.second.toString()) }
            }.let {
                post(it.build())
            }
        }
    }

    private fun isMultiPart() = params.any { it.second is File }

}

