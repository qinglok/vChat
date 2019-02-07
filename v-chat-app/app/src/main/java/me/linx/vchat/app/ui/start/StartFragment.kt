package me.linx.vchat.app.ui.start

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.model.AppViewModel
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig

class StartFragment : BaseFragment(), SunAnimationView.AnimationListener {
    private var clicking = false

    override fun setLayout() = R.layout.fragment_start

    override fun initView(toolBarConfig: ToolBarConfig, savedInstanceState: Bundle?){
        currentView.apply {
            sun_view.startAnimation(this@StartFragment)

            btn_start.setOnClickListener {
                if (!clicking) {
                    clicking = true

                    ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                        duration = 200
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)

                                // 取消全屏显示
                                ScreenUtils.setNonFullScreen(mActivity)

                                ViewModelProviders.of(mActivity).get(AppViewModel::class.java).appStartRoute { f ->
                                    fragmentManager?.beginTransaction()
                                        ?.replace(this@StartFragment.id, f, f::class.java.name)
                                        ?.commit()
                                }
                            }
                        })
                    }.start()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //全屏显示
        ScreenUtils.setFullScreen(mActivity)

        val window = mActivity.window
        // 刘海屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }

        SPUtils.getInstance().put(AppKeys.SP_is_first_in, false)
    }

    override fun onAnimationComplete() {
        ObjectAnimator.ofFloat(btn_start, "alpha", 0f, 1f).setDuration(500).start()
    }
}