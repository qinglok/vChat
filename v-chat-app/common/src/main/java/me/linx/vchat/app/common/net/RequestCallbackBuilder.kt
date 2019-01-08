package me.linx.vchat.app.common.net

import io.reactivex.Observable

fun <T> Observable<JsonResult<T>>.call(init: RequestCallbackBuilder<T>.() -> Unit) {
    val builder = RequestCallbackBuilder<T>()
    init.invoke(builder)
    builder.build(this)
}

class RequestCallbackBuilder<T>() {
    private val callback = object : RequestCallback<T>() {}

    internal fun build(observable: Observable<JsonResult<T>>) {
        observable.compose(NetworkScheduler.compose()).subscribe(callback)
    }

    fun start(onStart: () -> Unit): RequestCallbackBuilder<T> {
        callback.start = onStart
        return this
    }

    fun finish(onFinish: () -> Unit): RequestCallbackBuilder<T> {
        callback.finish = onFinish
        return this
    }

    fun success(onSuccess: (JsonResult<T>) -> Unit): RequestCallbackBuilder<T> {
        callback.success = onSuccess
        return this
    }

    fun failure(onFailure: (Int, String?) -> Unit): RequestCallbackBuilder<T> {
        callback.failure = onFailure
        return this
    }

    fun error(onError: (Throwable) -> Unit): RequestCallbackBuilder<T> {
        callback.error = onError
        return this
    }
}