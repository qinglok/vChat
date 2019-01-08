package me.linx.vchat.app.common.net

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class RequestCallback<T> : Observer<JsonResult<T>> {
    var start: () -> Unit = {}
    var finish: () -> Unit = {}
    var success: (JsonResult<T>) -> Unit = {}
    var failure: (Int, String?) -> Unit = { code, msg -> }
    var error: (Throwable) -> Unit = {}

    /**
     * 开始
     */
    override fun onSubscribe(d: Disposable) {
        start()
    }

    override fun onNext(t: JsonResult<T>) {
        if (t.code == 0)
            success(t)
        else
            failure(t.code, t.msg)
    }

    //完成
    override fun onComplete() {
        finish()
    }

    override fun onError(e: Throwable) {
        error(e)
        finish()
    }

}