package me.linx.vchat.app.utils

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