package me.linx.vchat.app.ec.start

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.properties.Delegates

/**
 * 太阳动画View
 */
class SunAnimationView : View {
    private var listener: AnimationListener by Delegates.notNull()

    // 屏幕宽
//    private var screenWidth = 0F
    // 屏幕高
//    private var measuredHeight = 0F
    // 屏幕中心 X
//    private var measuredWidth / 2 = 0F
    // 屏幕中心 Y
//    private var measuredHeight / 2 = 0F
    // 画笔
    private val mPaint = Paint()

    // 一个无限循环动画，只是用来更新View
    private val animatorSwitch = ValueAnimator.ofFloat(0f, 1f)!!

    // 是否绘制圆环
    private var isDrawRing = false
    // 是否绘制弧线
    private var isDrawArcLine = false
    // 是否绘制太阳
    private var isDrawSun = false
    // 是否绘制太阳阴影
    private var isDrawSunShadow = false
    // 是否绘制云层
    private var isDrawCloud = false
    // 是否绘制云层阴影
    private var isDrawCloudShadow = false

    // 圆环颜色
    private val ringColor = Color.parseColor("#FFD14A")
    // 圆环最大直径
    private var ringMaxDia = 0F
    // 圆环动画宽度
    private var ringWidth = 0f
    // 圆环2动画宽度
    private var ring2Width = 0f

    // 外弧控制
    private var outSideArcRectF: RectF by Delegates.notNull()
    // 外弧开始角度
    private var outSideArcStartAngle = 0F
    // 外弧角度长度（正数=顺时针，负数=逆时针）
    private var outSideArcAngleLength = 0F

    // 内弧控制
    private var inSideArcRectF: RectF by Delegates.notNull()
    // 内弧开始角度
    private var inSideArcStartAngle = 0F
    // 内弧角度长度（正数=顺时针，负数=逆时针）
    private var inSideArcAngleLength = 0F

    // 太阳旋转角度
    private var sunRotateAngle = 0f
    // 太阳直径
    private var sunWidth = 0f
    // 太阳光环控制
    private var sunFlowerRectF: RectF  by Delegates.notNull()
    // 太阳光环阴影 1
    private var sunFlowerLinearGradient: LinearGradient by Delegates.notNull()
    // 太阳光环阴影 2
    private var sumFlowerLinearGradient: LinearGradient by Delegates.notNull()

    // 太阳阴影颜色
    private val sunShadowColor = Color.parseColor("#bac3c3")
    // 太阳阴影控制
    private var sunShadowRectF: RectF by Delegates.notNull()

    // 云层颜色渐变
    private var cloudLinearGradient: LinearGradient by Delegates.notNull()
    // 云层由5个圆形组成，CircleInfo为每个圆形封装了信息
    private var circleInfoTopOne: CircleInfo by Delegates.notNull()
    private var circleInfoTopTwo: CircleInfo by Delegates.notNull()
    private var circleInfoBottomOne: CircleInfo by Delegates.notNull()
    private var circleInfoBottomTwo: CircleInfo by Delegates.notNull()
    private var circleInfoBottomThree: CircleInfo by Delegates.notNull()
    // 云层绘制路径
    private var cloudPath: Path by Delegates.notNull()

    // 云层阴影颜色
    private val cloudShadowColor = Color.parseColor("#bc9a31")
    // 云层阴影透明度，用于淡入动画
    private var cloudShadowAlpha = 0
    // 云层阴影控制
    private var cloudShadowRectF: RectF by Delegates.notNull()
    // 云层阴影绘制路径
    private var cloudShadowPath: Path by Delegates.notNull()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,defStyleAttr)

    private fun init() {
//        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val point = Point()
//        windowManager.defaultDisplay.getRealSize(point)

//        screenWidth = point.x.toFloat()
//        measuredHeight = point.y.toFloat()

//        screenWidth = measuredWidth.toFloat()
//        measuredHeight = measuredHeight.toFloat()

//        measuredWidth / 2 = (measuredWidth / 2)
//        measuredHeight / 2 = (measuredHeight / 2)

        // 去除锯齿
        mPaint.isAntiAlias = true
        mPaint.isDither = true

        //圆环最大直径为屏幕一半
        ringMaxDia = measuredWidth / 2f

        outSideArcStartAngle = 270f
        outSideArcAngleLength = 0f
        outSideArcRectF = RectF()
        outSideArcRectF.set(
            measuredWidth / 2 - ringMaxDia / 3,
            measuredHeight / 2 - ringMaxDia / 3,
            measuredWidth / 2 + ringMaxDia / 3,
            measuredHeight / 2 + ringMaxDia / 3
        )

        inSideArcStartAngle = -90f
        inSideArcAngleLength = 0f
        inSideArcRectF = RectF()
        inSideArcRectF.set(
            measuredWidth / 2 - ringMaxDia / 6,
            measuredHeight / 2 - ringMaxDia / 6,
            measuredWidth / 2 + ringMaxDia / 6,
            measuredHeight / 2 + ringMaxDia / 6
        )


        // 太阳光环由两个旋转不同角度正方形组成
        sunFlowerRectF = RectF()
//        sunFlowerRectF.set(
//            measuredWidth / 2 - ringMaxDia / 2,
//            measuredHeight / 2 - ringMaxDia / 2,
//            measuredWidth / 2 + ringMaxDia / 2,
//            measuredHeight / 2 + ringMaxDia / 2
//        )

        // 正方形1 的颜色渐变
        sunFlowerLinearGradient = LinearGradient(
            measuredWidth / 2 - ringMaxDia / 2,
            measuredHeight / 2 - ringMaxDia / 2,
            measuredWidth / 2 + ringMaxDia / 2,
            measuredHeight / 2 + ringMaxDia / 2,
            intArrayOf(Color.parseColor("#fff38e"), Color.parseColor("#ebb228"), Color.parseColor("#ae8200")),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.REPEAT
        )

        // 正方形2 的颜色渐变
        sumFlowerLinearGradient = LinearGradient(
            measuredWidth / 2 - ringMaxDia / 2,
            measuredHeight / 2 - ringMaxDia / 2,
            measuredWidth / 2 + ringMaxDia / 2,
            measuredHeight / 2 + ringMaxDia / 2,
            Color.parseColor("#f7b600"),
            Color.parseColor("#ae8200"),
            Shader.TileMode.REPEAT
        )

        sunShadowRectF = RectF()

        // 顶 1
        circleInfoTopOne =
                CircleInfo(measuredWidth / 2 * 1.1f, measuredHeight / 2 * 0.98f, measuredWidth / 2 * 0.18f, false)
        // 顶 2
        circleInfoTopTwo = CircleInfo(
            measuredWidth / 2 * 1.1f + measuredWidth / 2 * 0.18f * 1.15f,
            measuredHeight / 2 * 1.02f,
            measuredWidth / 2 / 5f,
            false
        )
        // 底 1
        circleInfoBottomOne = CircleInfo(
            measuredWidth / 2f,
            measuredHeight / 2 + measuredWidth / 2 * 0.2f,
            measuredWidth / 2 / 5f,
            false
        )
        // 底 2
        circleInfoBottomTwo =
                CircleInfo(
                    measuredWidth / 2 + measuredWidth / 2 / 5 * 0.9f,
                    measuredHeight / 2f + measuredWidth / 2 / 5,
                    measuredWidth / 2 / 5f,
                    false
                )
        // 底 3
        circleInfoBottomThree = CircleInfo(
            measuredWidth / 2 + measuredWidth / 2 / 5 * 0.9f + measuredWidth / 2 / 5 * 1.5f,
            measuredHeight / 2 + measuredWidth / 2 * 0.25f,
            measuredWidth / 2 / 6f,
            false
        )
        cloudLinearGradient = LinearGradient(
            circleInfoBottomOne.x - circleInfoBottomOne.radius,
            circleInfoTopOne.y - circleInfoTopOne.radius,
            circleInfoBottomThree.x + circleInfoBottomThree.radius,
            measuredHeight / 2 + measuredWidth / 7f,
            intArrayOf(Color.parseColor("#ECEADB"), Color.parseColor("#EFF0E2"), Color.parseColor("#D8D5C6")),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.REPEAT
        )
        cloudPath = Path()

        cloudShadowRectF = RectF()
        cloudShadowPath = Path()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        if (isDrawRing)
            drawRing(canvas)
        if (isDrawArcLine)
            drawArcLine(canvas)
        if (isDrawSun)
            drawSun(canvas)
        if (isDrawSunShadow)
            drawSunShadow(canvas)
        if (isDrawCloud)
            drawCloud(canvas)
        if (isDrawCloudShadow)
            drawCloudShadow(canvas)
    }

    // 绘制圆环：第一个黄色，第二个白色
    private fun drawRing(canvas: Canvas) {
        mPaint.shader = null
        mPaint.strokeWidth = 0f
        mPaint.style = Paint.Style.FILL//填充
        mPaint.color = ringColor
        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, ringWidth / 2, mPaint)
        mPaint.color = Color.WHITE
        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, ring2Width / 2, mPaint)
    }

    // 绘制弧线：外环和内环
    private fun drawArcLine(canvas: Canvas) {
        mPaint.color = ringColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND

        mPaint.strokeWidth = ringMaxDia / 10
        canvas.drawArc(outSideArcRectF, outSideArcStartAngle, outSideArcAngleLength, false, mPaint)

        mPaint.strokeWidth = ringMaxDia / 20
        canvas.drawArc(inSideArcRectF, inSideArcStartAngle, inSideArcAngleLength, false, mPaint)
    }

    // 绘制太阳、光环
    private fun drawSun(canvas: Canvas) {
        mPaint.strokeWidth = 0f
        mPaint.style = Paint.Style.FILL
        mPaint.color = ringColor
        mPaint.shader = sunFlowerLinearGradient
        canvas.save()
        canvas.rotate(sunRotateAngle, measuredWidth / 2f, measuredHeight / 2f)
        canvas.drawRect(sunFlowerRectF, mPaint)
        canvas.rotate(45f, measuredWidth / 2f, measuredHeight / 2f)
        mPaint.shader = sumFlowerLinearGradient
        canvas.drawRect(sunFlowerRectF, mPaint)
        canvas.restore()
        mPaint.shader = null
        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, sunWidth / 2, mPaint)
    }

    // 绘制太阳阴影
    private fun drawSunShadow(canvas: Canvas) {
        mPaint.color = sunShadowColor
        mPaint.style = Paint.Style.FILL
        canvas.drawOval(sunShadowRectF, mPaint)
    }

    // 绘制云层
    private fun drawCloud(canvas: Canvas) {
        cloudPath.reset()
        mPaint.shader = cloudLinearGradient
        if (circleInfoTopOne.isCanDraw)
            cloudPath.addCircle(
                circleInfoTopOne.x,
                circleInfoTopOne.y,
                circleInfoTopOne.radius,
                Path.Direction.CW
            )//顶1
        if (circleInfoTopTwo.isCanDraw)
            cloudPath.addCircle(
                circleInfoTopTwo.x,
                circleInfoTopTwo.y,
                circleInfoTopTwo.radius,
                Path.Direction.CW
            )//顶2
        if (circleInfoBottomOne.isCanDraw)
            cloudPath.addCircle(
                circleInfoBottomOne.x,
                circleInfoBottomOne.y,
                circleInfoBottomOne.radius,
                Path.Direction.CW
            )//左下1
        if (circleInfoBottomTwo.isCanDraw)
            cloudPath.addCircle(
                circleInfoBottomTwo.x,
                circleInfoBottomTwo.y,
                circleInfoBottomTwo.radius,
                Path.Direction.CW
            )//底部2
        if (circleInfoBottomThree.isCanDraw)
            cloudPath.addCircle(
                circleInfoBottomThree.x,
                circleInfoBottomThree.y,
                circleInfoBottomThree.radius,
                Path.Direction.CW
            )//底3

        canvas.save()
        canvas.clipRect(0f, 0f, measuredWidth.toFloat(), measuredHeight / 2f + measuredWidth / 7f)
        canvas.drawPath(cloudPath, mPaint)
        canvas.restore()
        mPaint.shader = null
    }

    // 绘制云层阴影
    private fun drawCloudShadow(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = cloudShadowColor
        mPaint.alpha = cloudShadowAlpha
        canvas.save()
        canvas.clipRect(0f, measuredHeight / 2f + measuredWidth / 7f, measuredWidth.toFloat(), measuredHeight.toFloat())
        cloudShadowRectF.set(
            measuredWidth / 2 - ringMaxDia / 2,
            measuredHeight / 2 - ringMaxDia / 2,
            measuredWidth / 2 + ringMaxDia / 2,
            measuredHeight / 2 + ringMaxDia / 2
        )
        cloudShadowPath.reset()
        cloudShadowPath.moveTo(circleInfoBottomOne.x, measuredHeight / 2 + measuredWidth / 7f)
        cloudShadowPath.arcTo(cloudShadowRectF, 15f, 45f, false)
        canvas.drawPath(cloudShadowPath, mPaint)
        canvas.restore()
        mPaint.alpha = 255
    }

    // 开始动画
    fun startAnimation(listener: AnimationListener) {
        this.listener = listener
        post {
            init()
            animatorSwitch.repeatCount = ValueAnimator.INFINITE
            animatorSwitch.addUpdateListener {
                invalidate()
            }
            animatorSwitch.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    startRingAnimation()
                }
            })
            animatorSwitch.start()
        }
    }

    // 圆环动画
    private fun startRingAnimation() {
        isDrawRing = true

        val animator = ValueAnimator.ofFloat(ringWidth, ringMaxDia)
        animator.duration = 500
        animator.addUpdateListener {
            ringWidth = it.animatedValue as Float
        }
        animator.start()


        // 内环直径达到最大值的80%，开始弧线动画
        val critical = ringMaxDia * 0.8

        val animator2 = ValueAnimator.ofFloat(ring2Width, ringMaxDia)
        animator2.duration = 500
        animator2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                isDrawRing = false
            }
        })
        animator2.addUpdateListener {
            ring2Width = it.animatedValue as Float
            if (!isDrawArcLine && ring2Width > critical) {
                startArcLineAnimation()
            }
        }
        animator2.startDelay = 200
        animator2.start()
    }

    // 弧线动画
    private fun startArcLineAnimation() {
        isDrawArcLine = true

        //外弧长度控制  -90
        val outSideLengthAnimator = ValueAnimator.ofFloat(outSideArcAngleLength, -270f, 0f)
        outSideLengthAnimator.duration = 500
        outSideLengthAnimator.addUpdateListener { animation ->
            outSideArcAngleLength = animation.animatedValue as Float
        }
        outSideLengthAnimator.start()

        //外弧位移控制  270
        val outSideOffsetAnimator = ValueAnimator.ofFloat(outSideArcStartAngle, 180f, -90f)
        outSideOffsetAnimator.duration = 500
        outSideOffsetAnimator.addUpdateListener { animation ->
            outSideArcStartAngle = animation.animatedValue as Float
        }
        outSideOffsetAnimator.start()


        //内弧长度控制
        val inSideLengthAnimator = ValueAnimator.ofFloat(inSideArcAngleLength, 180f, 0f)
        inSideLengthAnimator.duration = 500
        inSideLengthAnimator.addUpdateListener { animation ->
            inSideArcAngleLength = animation.animatedValue as Float
        }
        inSideLengthAnimator.startDelay = 200
        inSideLengthAnimator.start()

        //内弧位移控制
        val inSideOffsetAnimator = ValueAnimator.ofFloat(inSideArcStartAngle, 90f, 270f)
        inSideOffsetAnimator.duration = 500
        inSideOffsetAnimator.addUpdateListener { animation ->
            inSideArcStartAngle = animation.animatedValue as Float
            if (!isDrawSun && inSideArcStartAngle > 180f) {
                startSunAnimation()
            }
        }
        inSideOffsetAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                isDrawArcLine = false
            }
        })
        inSideOffsetAnimator.startDelay = 200
        inSideOffsetAnimator.start()
    }

    // 太阳、光环动画
    private fun startSunAnimation() {
        isDrawSun = true
        //太阳
        val sunAnim = ValueAnimator.ofFloat(sunWidth, ringMaxDia + 100, ringMaxDia)
        sunAnim.duration = 1000
        sunAnim.interpolator = AccelerateDecelerateInterpolator()
        sunAnim.addUpdateListener { animation -> sunWidth = animation.animatedValue as Float }
        sunAnim.start()

        //太阳光环
        val flowerAnim = ValueAnimator.ofFloat(0f, ringMaxDia, ringMaxDia * 0.9f)
        flowerAnim.duration = 1000
        flowerAnim.interpolator = AccelerateDecelerateInterpolator()
        flowerAnim.addUpdateListener { animation ->
            val width = animation.animatedValue as Float
            sunFlowerRectF.set(
                measuredWidth / 2 - width / 2,
                measuredHeight / 2 - width / 2,
                measuredWidth / 2 + width / 2,
                measuredHeight / 2 + width / 2
            )
        }
        flowerAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                startSunRotateAnimation()
            }
        })
        flowerAnim.startDelay = 100
        flowerAnim.start()

        this.postDelayed({ startSunShadowAnimation() }, 200)
        this.postDelayed({ startCloud() }, 300)
    }

    // 太阳旋转动画
    private fun startSunRotateAnimation() {
        val rotateAnim = ValueAnimator.ofFloat(0f, 360f)
        rotateAnim.duration = 30 * 1000
        rotateAnim.repeatCount = ValueAnimator.INFINITE
        rotateAnim.interpolator = LinearInterpolator()
        rotateAnim.addUpdateListener { animation -> sunRotateAngle = animation.animatedValue as Float }
        rotateAnim.start()
    }

    // 太阳阴影动画
    private fun startSunShadowAnimation() {
        isDrawSunShadow = true
        val valueAnimator = ValueAnimator.ofFloat(0f, ringMaxDia, ringMaxDia * 0.8f).setDuration(1000)
        valueAnimator.addUpdateListener { animation ->
            val sunShadowWidth = animation.animatedValue as Float
            sunShadowRectF.set(
                measuredWidth / 2 - sunShadowWidth / 2,
                measuredHeight / 2 + ringMaxDia,
                measuredWidth / 2 + sunShadowWidth / 2,
                measuredHeight / 2 + ringMaxDia + ringMaxDia / 25
            )
        }
        valueAnimator.setFloatValues()
        valueAnimator.start()
    }

    // 云层所有动画
    private fun startCloud() {
        isDrawCloud = true
        startCloudTemplate(0, circleInfoBottomOne)
        startCloudTemplate(100, circleInfoBottomTwo)
        startCloudTemplate(200, circleInfoBottomThree)
        startCloudTemplate(350, circleInfoTopOne)
        startCloudTemplate(450, circleInfoTopTwo)
        this.postDelayed({ startCloudShadow() }, 600)
    }

    // 云层圆球动画
    private fun startCloudTemplate(delay: Long, circleInfo: CircleInfo) {
        val valueAnimator = ValueAnimator.ofObject(
            CircleTypeEvaluator(circleInfo),
            CircleInfo(circleInfo.x, circleInfo.y + circleInfo.radius, 0f, false),
            CircleInfo(circleInfo.x, circleInfo.y, circleInfo.radius, false)
        )
        valueAnimator.duration = 600
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                circleInfo.isCanDraw = true
            }
        })
        valueAnimator.setObjectValues(
            CircleInfo(circleInfo.x, circleInfo.y + circleInfo.radius, 0f, false),
            CircleInfo(circleInfo.x, circleInfo.y, circleInfo.radius, false)
        )
        valueAnimator.startDelay = delay
        valueAnimator.start()
    }

    // 云层阴影动画
    private fun startCloudShadow() {
        isDrawCloudShadow = true
        val valueAnimator = ValueAnimator.ofInt(0, 255).setDuration(600)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation -> cloudShadowAlpha = animation.animatedValue as Int }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                listener.onAnimationComplete()
            }
        })
        valueAnimator.start()
    }

    fun end() {
        animatorSwitch.end()
    }

    interface AnimationListener {
        fun onAnimationComplete()
    }
}