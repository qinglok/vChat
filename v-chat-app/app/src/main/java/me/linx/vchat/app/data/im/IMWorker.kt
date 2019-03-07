package me.linx.vchat.app.data.im

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import me.linx.vchat.app.constant.AppKeys

class IMWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        SPUtils.getInstance().getLong(AppKeys.SP_current_user_id, 0L).also {
            if (it > 0) {
                val pm = Utils.getApp().getSystemService(Context.POWER_SERVICE) as PowerManager
                val wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.canonicalName)
                wakeLock.acquire(30 * 1000L /*1 minutes*/)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utils.getApp().startForegroundService(Intent(Utils.getApp(), IMService::class.java))
                } else {
                    Utils.getApp().startService(Intent(Utils.getApp(), IMService::class.java))
                }
                IMService.sendHeartBeat()
            }
        }
        return Result.success()
    }
}