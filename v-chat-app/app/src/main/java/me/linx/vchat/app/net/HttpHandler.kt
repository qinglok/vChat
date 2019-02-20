package me.linx.vchat.app.net

import me.linx.vchat.app.widget.loader.LoaderDialogFragment

data class HttpHandler<T>(
    var onStart: () -> Unit = {},
    var onFinish: () -> Unit = {},
    var onSuccess: (T) -> Unit = {},
    var onError: (Throwable) -> Unit = {},
    var onCancel: () -> Unit = {},
    var withLoader: Boolean = false
) {
    private var tag : Any = 0
    private var loader: LoaderDialogFragment? = null
    var isCancel = false

    fun showLoader() {
        loader = LoaderDialogFragment()
        loader?.showWithOnDismiss {
            cancel()
        }
    }

    fun hideLoader() {
        loader?.dismiss()
    }

    fun setTag(tag : Any){
        this.tag = tag
    }

    private fun cancel() {
        isCancel = true
        HttpWrapper.cancel(tag)
    }
}