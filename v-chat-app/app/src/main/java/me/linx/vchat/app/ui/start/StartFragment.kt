package me.linx.vchat.app.ui.start

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.BarUtils
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.data.model.UserViewModel
import me.linx.vchat.app.widget.base.BaseFragment

class StartFragment : BaseFragment(), SunAnimationView.AnimationListener {
    private val viewModel by lazy {
        ViewModelProviders.of(mActivity).get(UserViewModel::class.java)
    }
    private var clicking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = mActivity.window

        //全屏显示
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // 刘海屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
    }

    override fun setLayout() = R.layout.fragment_start

    override fun initView(view : View, savedInstanceState: Bundle?) {
        view.sun_view.startAnimation(this)

        view.btn_start.setOnClickListener {
            if (!clicking) {
                clicking = true

               ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
                    duration = 200
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)

                            view.sun_view.end()

                            //启用透明状态栏
                            BarUtils.setStatusBarAlpha(mActivity, 0)

                            viewModel.appStartRoute(this@StartFragment)
                        }
                    })
                }.start()
            }
        }
    }

    override fun onAnimationComplete() {
        ObjectAnimator.ofFloat(btn_start, "alpha", 0f, 1f).setDuration(500).start()
    }
}