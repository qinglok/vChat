package me.linx.vchat.app.net

data class HttpCallback<T>(
    var start: () -> Unit = {},
    var finish: () -> Unit = {},
    var success: (T) -> Unit = {},
    var error: (Throwable) -> Unit = {}
)