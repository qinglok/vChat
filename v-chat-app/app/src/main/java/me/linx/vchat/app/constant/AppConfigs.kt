package me.linx.vchat.app.constant

import android.annotation.SuppressLint
import android.app.NotificationManager
import com.blankj.utilcode.util.Utils
import me.linx.vchat.app.R
import me.linx.vchat.app.widget.NotifyManager

@SuppressLint("InlinedApi")
object AppConfigs {
    val connectTimeout by lazy { 8000L }
    val readTimeout by lazy { 8000L }
    val writeTimeout by lazy { 8000L }

    val databaseName by lazy { "vchat_db" }

    val notifyChannelChat by lazy {
        NotifyManager.NotifyChannelBean(
            "chat",
            Utils.getApp().getString(R.string.notify_channel_chat),
            NotificationManager.IMPORTANCE_MAX
        )
    }

}