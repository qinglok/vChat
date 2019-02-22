package me.linx.vchat.app.data.im

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import com.blankj.utilcode.util.Utils

class IMGuardService : Service() {
    private var job: Thread? = null

    override fun onCreate() {
        super.onCreate()

        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "IMService"
            val channelName = "聊天服务"
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val builder = Notification.Builder(this, channelId)
            notification = builder
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(
                    1,
                    notification
                ) //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
            }
        } else {
            @Suppress("DEPRECATION")
            notification = Notification.Builder(this)
                .build()
            startForeground(1, notification)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (job == null || !job!!.isAlive) {
            job = object : Thread() {
                override fun run() {
                    super.run()
                    while (true) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(Intent(Utils.getApp(), IMService::class.java))
                        } else {
                            startService(Intent(Utils.getApp(), IMService::class.java))
                        }

                        try {
                            sleep(8000L)
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            job!!.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?) = null

}