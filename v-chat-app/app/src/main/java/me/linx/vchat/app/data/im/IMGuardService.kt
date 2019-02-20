package me.linx.vchat.app.data.im

import android.app.Service
import android.content.Intent
import com.blankj.utilcode.util.ServiceUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IMGuardService : Service() {
    private  var job: Job? = null

    override fun onCreate() {
        super.onCreate()

        job = GlobalScope.launch {
            while (true) {
                if (!ServiceUtils.isServiceRunning(IMService::class.java)) {
                    ServiceUtils.startService(IMService::class.java)
                }
                delay(8000)
            }
        }
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}