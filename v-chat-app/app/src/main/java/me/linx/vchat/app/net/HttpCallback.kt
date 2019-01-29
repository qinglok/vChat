package me.linx.vchat.app.net

class HttpCallback<T>(
    var start: () -> Unit = {},
    var finish: () -> Unit = {},
    var success: (T) -> Unit = {},
    var error: (Throwable) -> Unit = {}
)