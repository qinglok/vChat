@file:Suppress("unused")

package me.linx.vchat.app.utils

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.blankj.utilcode.util.Utils
import com.google.android.material.snackbar.Snackbar
import me.linx.vchat.app.R

fun View.snackbar(msg: String?) {
    Snackbar.make(this, msg ?: "", Snackbar.LENGTH_LONG).show()
}

fun View.snackbar(@StringRes msg: Int) {
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG).show()
}

fun View.snackbarSuccess(msg: String?) {
    snackbarWithColor(msg, Utils.getApp().getColor(R.color.success))
}

fun View.snackbarSuccess(@StringRes msg: Int) {
    snackbarWithColor(msg, Utils.getApp().getColor(R.color.success))
}

fun View.snackbarFailure(msg: String?) {
    snackbarWithColor(msg, Utils.getApp().getColor(R.color.failure))
}

fun View.snackbarFailure(@StringRes msg: Int) {
    snackbarWithColor(msg, Utils.getApp().getColor(R.color.failure))
}

fun View.snackbarError(msg: String?) {
    snackbarWithColor(msg, Utils.getApp().getColor(R.color.error))
}

fun View.snackbarError(@StringRes msg: Int) {
    snackbarWithColor(msg, Utils.getApp().getColor(R.color.error))
}

fun View.snackbarWithColor(msg: String?, @ColorInt color : Int) {
    val snackbar = Snackbar.make(this, msg ?: "", Snackbar.LENGTH_LONG)
    todo(snackbar, color)
}

fun View.snackbarWithColor(@StringRes msg: Int, @ColorInt color : Int) {
    val snackbar = Snackbar.make(this, msg , Snackbar.LENGTH_LONG)
    todo(snackbar, color)
}

private fun todo(snackbar : Snackbar, @ColorInt color : Int){
    val view = snackbar.view
    view.setBackgroundColor(color)
    snackbar.show()
}

