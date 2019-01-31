@file:Suppress("unused")

package me.linx.vchat.app.utils

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

/**
 *  线程切换 for Observable
 */
fun <R> Observable<R>.transfor(): Observable<R> = compose {
    it.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 *  线程切换 for Maybe
 */
fun <R> Maybe<R>.transfor(): Maybe<R> = compose {
    it.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 *  线程切换 for Single
 */
fun <R> Single<R>.transfor(): Single<R> = compose {
    it.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 *  线程切换 for Completable
 */
fun Completable.transfor(): Completable = compose {
    it.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 *  简单IO任务 for Completable
 */
fun rxRun(action: () -> Unit) =
    Completable.create {
        action()
        it.onComplete()
    }.transfor().subscribe(object : DisposableCompletableObserver() {
        override fun onComplete() {
            dispose()
        }

        override fun onError(e: Throwable) {
            dispose()
        }
    })

/**
 *  简单IO任务 for Single
 */
fun <R> Single<R>.rxRun(action: (R?) -> Unit) =
    this.transfor().subscribe(object : DisposableSingleObserver<R>() {
        override fun onSuccess(t: R) {
            action(t)
            dispose()
        }

        override fun onError(e: Throwable) {
            action(null)
            dispose()
        }
    })

/**
 *  简单IO任务 for Maybe
 */
fun <R> Maybe<R>.rxRun(action: (R?) -> Unit) =
    this.transfor().subscribe(object : DisposableMaybeObserver<R>() {

        override fun onSuccess(t: R) {
            action(t)
            dispose()
        }

        override fun onComplete() {
            action(null)
            dispose()
        }

        override fun onError(e: Throwable) {
            dispose()
        }
    })

/**
 *  简单IO任务 for Observable
 */
fun <R> Observable<R>.rxRun(action: (R) -> Unit) =
    this.transfor().subscribe(object : DisposableObserver<R>() {
        override fun onComplete() {
            dispose()
        }

        override fun onNext(t: R) {
            action(t)
        }

        override fun onError(e: Throwable) {
            dispose()
        }
    })