package me.linx.vchat.app.data.im.handler

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.constant.CodeMap
import me.linx.vchat.app.data.entity.Message
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.model.FragmentMessageDetailViewModel
import me.linx.vchat.app.data.repository.MessageRepository
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.ui.main.message.MessageDetailFragment
import me.linx.vchat.app.utils.launch
import me.linx.vchat.app.utils.then
import me.linx.vchat.app.widget.NotifyManager
import me.linx.vchat.core.packet.Packet


class TextPacketHandler : SimpleChannelInboundHandler<Packet.TextPacket>() {

    override fun messageReceived(p0: ChannelHandlerContext?, msg: Packet.TextPacket?) {
        GlobalScope.launch {
            val currentUser = UserRepository.instance.getByAsync(SPUtils.getInstance().getLong(AppKeys.SP_current_user_id)).await()
            val targetUser: User? = withContext(Dispatchers.Default) {
                var user = UserRepository.instance.getByAsync(msg?.fromId ?: 0L).await()
                if (user == null) {
                    UserRepository.instance.getUserProfile(currentUser?.token ?: "", msg?.fromId ?: 0L, 0L) {
                        onSuccess = { result ->
                            if (result.code == CodeMap.Yes) {
                                user = result.data
                                UserRepository.instance.saveAsync(user).launch()
                            }
                        }
                    }
                }
                user
            }

            targetUser?.let {
                Message().apply {
                    fromId = msg!!.fromId
                    fromName = targetUser.nickname
                    fromAvatar = targetUser.avatar
                    toId = msg.toId
                    toName = currentUser?.nickname
                    toAvatar = currentUser?.avatar
                    content = msg.msg
                    read = false
                    sent = true
                    updateTime = System.currentTimeMillis()
                }.also { message ->
                    MessageRepository.instance.saveAsync(message).then {
                        var isNotify = true

                        // App 是否处于前台
                        val isAppForeground = AppUtils.isAppForeground()

                        if (isAppForeground) {
                            AppActivity.instance.also { activity ->
                                activity.supportFragmentManager
                                    .findFragmentByTag(MessageDetailFragment::class.java.name)
                                    .also { fragment ->
                                        // 处于聊天界面
                                        if (fragment != null && fragment.isAdded && fragment.userVisibleHint) {
                                            val viewModel = ViewModelProviders.of(fragment)
                                                .get(FragmentMessageDetailViewModel::class.java)
                                            // 聊天对象与新消息的发送者一致
                                            if (viewModel.targetUser.bizId == message.fromId) {
                                                isNotify = false
                                                viewModel.newMessage(message)
                                            }
                                        }
                                    }
                            }
                        }

                        if (isNotify) {
                            notify(createIntent(isAppForeground, targetUser), message, currentUser, targetUser)
                        }
                    }
                }
            }
        }
    }

    private fun notify(intent : PendingIntent?, msg: Message, currentUser: User?, targetUser: User) {
          GlobalScope.launch {
            val bitmap = UserRepository.instance.loadAvatarBitmapAsync(targetUser.avatar).await()
            val unRead = MessageRepository.instance.queryUnReadAsync(currentUser?.bizId ?: 0L).await()
            val content = if (unRead > 1) {
                "[${unRead}条] ${targetUser.nickname}:${msg.content ?: ""}"
            } else {
                "${targetUser.nickname}:${msg.content ?: ""}"
            }

            NotifyManager.sendChatMsg(
                targetUser.nickname ?: "",
                content,
                targetUser.bizId?.toInt() ?: 0,
                bitmap,
                intent
            )
        }
    }

    private fun createIntent(isAppForeground: Boolean, targetUser: User) : PendingIntent?{
        if (isAppForeground){
            val resultIntent = Intent(AppKeys.ACTION_new_message).apply {
                putExtra(AppKeys.KEY_target_user, targetUser)
            }
            return PendingIntent.getBroadcast(Utils.getApp(),targetUser.bizId?.toInt() ?: 0 ,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT )
        }else{
            val resultIntent = Intent(Utils.getApp(), AppActivity::class.java).apply {
                action = AppKeys.ACTION_new_message
                putExtra(AppKeys.KEY_target_user, targetUser)
            }
            val stackBuilder = TaskStackBuilder.create(Utils.getApp()).apply {
                addNextIntent(resultIntent)
            }
            return stackBuilder.getPendingIntent(
                targetUser.bizId?.toInt() ?: 0 ,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


//        return resultPendingIntent
    }
}