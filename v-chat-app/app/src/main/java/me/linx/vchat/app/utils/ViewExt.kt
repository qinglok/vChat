package me.linx.vchat.app.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 添加假状态栏
 */
//fun View.fitStatusBar() {
//    //状态栏高度
//    val statusHeight = BarUtils.getStatusBarHeight()
//    //因为将状态栏设置成透明，所以要为工具栏添加等同于状态栏的高度
//    val lp = layoutParams
//    lp.height += statusHeight
//    //并且添加等同于状态栏paddingTop
//    setPadding(
//        paddingLeft,
//        paddingTop + statusHeight,
//        paddingRight,
//        paddingBottom
//    )
//}

/**
 * 显示软键盘
 */
@Suppress("unused")
fun View.showSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({
        requestFocus()
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }, 200)
}

/**
 * 切换软键盘
 */
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