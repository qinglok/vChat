package me.linx.vchat.app.ui.main

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_head_img.*
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.utils.GlideApp

class HeadImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_head_img)

        val imgUrl = intent.getStringExtra(AppKeys.key_user_head_img)

        GlideApp.with(this)
            .load(Api.baseFileDir + imgUrl)
            .into(iv_photo)

        iv_photo.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

        //全屏显示
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // 刘海屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
    }
}