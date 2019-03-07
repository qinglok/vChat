package me.linx.vchat.app.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


fun <T> Deferred<T?>.launch(context: CoroutineContext = EmptyCoroutineContext) {
    GlobalScope.launch(context) {
        await()
    }
}

fun <T> Deferred<T?>.then(context: CoroutineContext = EmptyCoroutineContext, action: (T?) -> Unit) {
    GlobalScope.launch(context) {
        action(await())
    }
}

val handler by lazy {
    Handler(Looper.getMainLooper())
}

fun runOnMain(action: () -> Unit){
    handler.post(action)
}

