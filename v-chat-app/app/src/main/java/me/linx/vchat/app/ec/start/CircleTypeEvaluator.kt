package me.linx.vchat.app.ec.start

import android.animation.TypeEvaluator

/**
 * 云层圆球动画估值器
 */
class CircleTypeEvaluator(private val mCircleInfo: CircleInfo) : TypeEvaluator<CircleInfo> {

    override fun evaluate(fraction: Float, startValue: CircleInfo, endValue: CircleInfo): CircleInfo {
        val y = startValue.y + fraction * (endValue.y - startValue.y)
        val radius = startValue.radius + fraction * (endValue.radius - startValue.radius)
        mCircleInfo.setCircleInfo(mCircleInfo.x, y, radius)
        return mCircleInfo
    }
}
