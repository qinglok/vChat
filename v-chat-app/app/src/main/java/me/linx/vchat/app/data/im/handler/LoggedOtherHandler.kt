package me.linx.vchat.app.data.im.handler

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.entity.Message
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.im.IMGuardService
import me.linx.vchat.app.data.im.IMService
import me.linx.vchat.app.ui.sign.SignInFragment
import me.linx.vchat.core.packet.Packet

class LoggedOtherHandler : SimpleChannelInboundHandler<Packet.LoggedOtherPacket>() {

    override fun messageReceived(p0: ChannelHandlerContext?, p1: Packet.LoggedOtherPacket?) {
        SPUtils.getInstance().put(AppKeys.SP_current_user_id, 0L)
        ServiceUtils.stopService(IMService::class.java)
        ServiceUtils.stopService(IMGuardService::class.java)

        if (AppUtils.isAppForeground()) {
            AppActivity.instance?.let { activity ->
                GlobalScope.launch(Dispatchers.Main) {
                    MaterialAlertDialogBuilder(activity)
                        .setTitle(R.string.logged_on_other)
                        .setOnDismissListener {
                            activity.supportFragmentManager.popBackStack(
                                null,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                            activity.supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    SignInFragment(),
                                    SignInFragment::class.java.name
                                )
                                .commit()
                        }
                        .setPositiveButton(R.string.ok, null)
                        .show()
                }
            }
        }else{
            notifys()
        }
    }

     fun notifys() {
        val mBuilder = NotificationCompat.Builder(Utils.getApp())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(Utils.getApp().getString(R.string.logged_on_other))

// Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(Utils.getApp(), AppActivity::class.java)

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(Utils.getApp())
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(AppActivity::class.java)
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build())
    }
}