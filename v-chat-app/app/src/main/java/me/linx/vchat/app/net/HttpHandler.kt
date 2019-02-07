package me.linx.vchat.app.net

import me.linx.vchat.app.widget.loader.LoaderDialogFragment
import okhttp3.Call

data class HttpHandler<T>(
    var onStart: () -> Unit = {},
    var onFinish: () -> Unit = {},
    var onSuccess: (T) -> Unit = {},
    var onError: (Throwable) -> Unit = {},
    var onCancel: () -> Unit = {},
    var withLoader : Boolean = false
){
    private var loader : LoaderDialogFragment? = null
    private var call : Call? = null
    var isCancel = false

    fun showLoader(){
        loader = LoaderDialogFragment()
        loader?.showWithOnDismiss {
            cancel()
        }
    }

    fun hideLoader(){
        loader?.dismiss()
    }

    fun setupCall(c : Call){
        call = c
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun cancel(){
        isCancel = true
        call?.cancel()
    }
}