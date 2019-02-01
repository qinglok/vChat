package me.linx.vchat.app.ui.main

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.android.synthetic.main.activity_head_img.*
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.utils.GlideApp

class HeadImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //全屏显示
        ScreenUtils.setFullScreen(this)
        //启用透明状态栏
        BarUtils.setStatusBarColor(this, Color.argb(0, 0, 0, 0))

        // 刘海屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }

        setContentView(R.layout.activity_head_img)

        val imgUrl = intent.getStringExtra(AppKeys.KEY_user_head_img)

        GlideApp.with(this)
            .load(Api.baseFileDir + imgUrl)
            .into(iv_photo)

        iv_photo.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()


    }
}