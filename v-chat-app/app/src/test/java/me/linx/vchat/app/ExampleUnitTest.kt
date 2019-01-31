package me.linx.vchat.app

import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit showBigImg, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun maybeTest(){
        Maybe.create<String> {
            it.onSuccess("a")
            it.onComplete()
//            it.onError(RuntimeException("err..."))
        }.subscribe(object : MaybeObserver<String>{
            override fun onSuccess(t: String) {
                println(t)
            }

            override fun onComplete() {
                println("onComplete")
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                println(e.message)
            }
        })
    }
}
