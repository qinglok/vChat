package me.linx.vchat.app.data.im

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.blankj.utilcode.util.Utils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.getApp().startForegroundService(Intent(Utils.getApp(), IMService::class.java))
            } else {
                Utils.getApp().startService(Intent(Utils.getApp(), IMService::class.java))
            }
        }
    }
}