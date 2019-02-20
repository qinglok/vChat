package me.linx.vchat.app.widget

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.blankj.utilcode.util.Utils
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppConfigs

object NotifyManager {

    data class NotifyChannelBean(
        val channelId: String,
        val channelName: String,
        val importance: Int
    )

    fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(AppConfigs.notifyChannelChat)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(bean: NotifyChannelBean) {
        val channel = NotificationChannel(bean.channelId, bean.channelName, bean.importance).apply {
            enableVibration(true)
            vibrationPattern = longArrayOf(800L, 200L, 200L, 200L)
        }
        val notificationManager = Utils.getApp().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendChatMsg(title: String, msg: String, identifier: Int, fromUserAvatar: Bitmap, intent: PendingIntent?) {
        val manager = Utils.getApp().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(Utils.getApp(), AppConfigs.notifyChannelChat.channelId).apply {
            setContentTitle(title)
            setContentText(msg)
            setWhen(System.currentTimeMillis())
            setVisibility(VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.ic_message_black_24dp)
            setLargeIcon(fromUserAvatar)
            setAutoCancel(true)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            setVibrate(longArrayOf(800L, 200L, 200L, 200L))

            intent?.let {
                setContentIntent(intent)
            }
        }.build()
        manager.notify(identifier, notification)
    }

    fun clearByIdentifier(identifier: Int) {
        val manager = Utils.getApp().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        for (activeNotification in manager.activeNotifications) {
            if (activeNotification.id == identifier) {
                manager.cancel(activeNotification.id)
                break
            }
        }
    }

}