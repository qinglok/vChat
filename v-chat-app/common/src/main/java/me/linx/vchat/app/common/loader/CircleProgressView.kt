package me.linx.vchat.app.common.loader

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class CircleProgressView : View {

    private val mPaint = Paint()
    private val rect = RectF()

    private  val strokeWidth = 20f

    private val onePart = 30f
    private var startAngle = 0f
    private var sweepAngle = onePart

    private var sunRotateAngle = 0f

    private val ringColor = Color.parseColor("#d8e6fa")

    private var isDrawArcLine = false

    private lateinit var animatorSwitch: ValueAnimator

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun init() {
        // 去除锯齿
        mPaint.isAntiAlias = true
        mPaint.isDither = true

        val maxRingWidth = width - strokeWidth

        rect.set(
            width / 2f - maxRingWidth / 2,
            height / 2f - maxRingWidth / 2,
            width / 2f + maxRingWidth / 2,
            height / 2f + maxRingWidth / 2
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        if (isDrawArcLine) {
            drawArcLine(canvas)
        }
    }

    private fun drawArcLine(canvas: Canvas) {
        mPaint.color = ringColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = strokeWidth

        canvas.save()
        canvas.rotate(sunRotateAngle, width / 2f, height / 2f)
        canvas.drawArc(rect, startAngle, sweepAngle, false, mPaint)
        canvas.restore()
    }

    fun startAnimation() {
        init()

        animatorSwitch = ValueAnimator.ofFloat(0f, 1f)
        animatorSwitch.repeatCount = ValueAnimator.INFINITE
        animatorSwitch.addUpdateListener { invalidate() }
        animatorSwitch.start()

        startArcLineAnimation()

    }

    private fun startArcLineAnimation() {
        val rotateAnim = ValueAnimator.ofFloat(0f, 360f)
        rotateAnim.duration = 1000
        rotateAnim.repeatCount = ValueAnimator.INFINITE
        rotateAnim.interpolator = LinearInterpolator()
        rotateAnim.addUpdateListener { animation -> sunRotateAngle = animation.animatedValue as Float }
        rotateAnim.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                isDrawArcLine = true
            }
        })
        rotateAnim.start()

        val a = ValueAnimator.ofFloat()
        val b = ValueAnimator.ofFloat()

        a.setFloatValues(sweepAngle, 360f - onePart)
        a.duration = 600
        a.interpolator = FastOutSlowInInterpolator()
        a.addUpdateListener { animation ->
            sweepAngle = animation.animatedValue as Float
        }
        a.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                    b.setFloatValues(startAngle, startAngle + 360f - onePart*1.1F )
                    b.start()
            }
        })
        a.start()

        b.duration = 600
        b.interpolator = FastOutSlowInInterpolator()
        b.addUpdateListener { animation ->
            val v = animation.animatedValue as Float
            val c = v - startAngle
            startAngle = v

            sweepAngle -= c

        }
        b.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                    a.setFloatValues(sweepAngle, 360f - onePart)
                    a.start()
            }
        })
    }

    fun stopAnimation() {
        animatorSwitch.end()
        isDrawArcLine = false
    }
}