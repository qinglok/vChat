package me.linx.vchat.app.data.model

import android.app.Service
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.linx.vchat.app.BR
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.entity.Message
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.im.IMService
import me.linx.vchat.app.data.model.utils.DataBindingBaseQuickAdapter
import me.linx.vchat.app.data.model.utils.DataBindingBaseViewHolder
import me.linx.vchat.app.data.repository.MessageRepository
import me.linx.vchat.app.databinding.FragmentMessageDetailBinding
import me.linx.vchat.app.ui.main.message.MessageDetailFragment
import me.linx.vchat.app.utils.hideSoftInput
import me.linx.vchat.app.utils.launch
import me.linx.vchat.app.utils.then
import me.linx.vchat.app.widget.NotifyManager
import me.linx.vchat.app.widget.base.ToolBarConfig

class FragmentMessageDetailViewModel : ViewModel() {
    lateinit var obUser: User
    lateinit var targetUser: User
    private lateinit var adapter: DataBindingBaseQuickAdapter<Message>

    fun init(f: MessageDetailFragment, toolBarConfig: ToolBarConfig) {
        // 此处targetUser不能为空，开始MessageDetailFragment之前处理掉这类问题
        this.targetUser = f.arguments!!.getParcelable(AppKeys.KEY_target_user)!!
        ViewModelProviders.of(f.mActivity).get(AppViewModel::class.java).obUser.observeForever {
            obUser = it
        }

        toolBarConfig.apply {
            showDefaultToolBar = true
            title = targetUser.nickname
            enableBackOff = true
            onBackOffClick = {
                f.currentView.hideSoftInput()
                f.fragmentManager?.popBackStack()
            }
        }

        adapter = object : DataBindingBaseQuickAdapter<Message>(R.layout.item_message) {
            override fun bind(helper: DataBindingBaseViewHolder?, item: Message): HashMap<Int, Any> {
                return hashMapOf(
                    BR.msg to item,
                    BR.receivedGroupVisibility to if (item.fromId != obUser.bizId) View.VISIBLE else View.GONE,
                    BR.sentGroupVisibility to if (item.fromId == obUser.bizId) View.VISIBLE else View.GONE
                )
            }
        }

        DataBindingUtil.bind<FragmentMessageDetailBinding>(f.currentView)?.apply {
            this.rv.layoutManager = LinearLayoutManager(f.mActivity)
            adapter.bindToRecyclerView(rv)

            KeyboardUtils.registerSoftInputChangedListener(f.mActivity) {
                if (it > 0) {
                    GlobalScope.launch(Dispatchers.Main) {
                        adapter.getRecycler().scrollToPosition(adapter.data.size - 1)
                    }
                }
                f.currentView.setPadding(0,0,0,it)
            }

            this.btnSend.setOnClickListener {
                val content = etContent.text.toString()
                etContent.setText("")
//                etContent.hideSoftInput()
//                adapter.getRecycler().scrollToPosition(adapter.data.size - 1)

                Message().apply {
                    fromId = obUser.bizId
                    fromName = obUser.nickname
                    fromAvatar = obUser.avatar
                    toId = targetUser.bizId
                    toName = targetUser.nickname
                    toAvatar = targetUser.avatar
                    this.content = content
                    read = true
                    sent = false
                    updateTime = System.currentTimeMillis()
                }.also { msg ->

                    MessageRepository.instance.saveAsync(msg).then { newId ->
                        newId?.also {
                            msg.id = newId

                            GlobalScope.launch(Dispatchers.Main) {
                                adapter.addData(msg)
                                adapter.getRecycler().scrollToPosition(adapter.data.size - 1)
                            }

                            IMService.instance?.send(msg) { isSuccess ->
                                if (isSuccess) {
                                    msg.sent = true
                                    MessageRepository.instance.saveAsync(msg).launch()
                                }
                            }
                        }
                    }
                }
            }
        }

        MessageRepository.instance.getByFromAndToAsync(targetUser.bizId ?: 0L, obUser.bizId ?: 0L)
            .then(Dispatchers.Main) { list ->
                list?.let {
                    adapter.setNewData(list.toMutableList())
                    adapter.getRecycler().scrollToPosition(adapter.data.size - 1)
                }
                // 清除未读消息
                MessageRepository.instance.clearUnReadAsync(obUser.bizId ?: 0L).launch()
                // 清除对应通知栏
                NotifyManager.clearByIdentifier(targetUser.bizId?.toInt() ?: 0)
            }
    }

    /**
     *  收到新消息
     */
    fun newMessage(message: Message) {
        GlobalScope.launch(Dispatchers.Main) {
            adapter.addData(message)
            adapter.getRecycler().scrollToPosition(adapter.data.size - 1)
            val vibrator = Utils.getApp().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200L)
            }
        }
    }

}