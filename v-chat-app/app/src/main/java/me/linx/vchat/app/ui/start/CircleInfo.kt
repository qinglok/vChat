package me.linx.vchat.app.ui.start

/**
 * 组成云层的圆球信息包装
 */
data class CircleInfo(var x: Float, var y: Float, var radius: Float, var isCanDraw :Boolean) {

    fun setCircleInfo(x: Float, y: Float, radius: Float) {
        this.x = x
        this.   y = y
        this. radius = radius
    }
}
