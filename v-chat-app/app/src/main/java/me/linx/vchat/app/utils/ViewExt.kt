package me.linx.vchat.app.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * 显示软键盘
 */
fun View.showSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({
        requestFocus()
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }, 200)
}

fun View.showOrHideSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({
        requestFocus()
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }, 200)
}

/**
 * 隐藏软键盘
 */
fun View.hideSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}