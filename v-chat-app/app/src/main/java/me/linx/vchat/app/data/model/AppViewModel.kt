package me.linx.vchat.app.data.model


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.constant.CodeMap
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.im.IMGuardService
import me.linx.vchat.app.data.im.IMService
import me.linx.vchat.app.data.model.utils.ObservableViewModel
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.net.HttpTask
import me.linx.vchat.app.net.HttpWrapper
import me.linx.vchat.app.ui.main.MainFragment
import me.linx.vchat.app.ui.main.message.MessageDetailFragment
import me.linx.vchat.app.ui.sign.SignInFragment
import me.linx.vchat.app.ui.start.StartFragment
import me.linx.vchat.app.utils.runOnMain
import me.linx.vchat.app.utils.then
import me.linx.vchat.app.widget.base.BaseFragment


class AppViewModel : ObservableViewModel() {
    val obUser by lazy {
        MutableLiveData<User>()
    }

    private val newMessageReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                AppActivity.instance.supportFragmentManager.also { fm ->
                    val topShowFragment = FragmentUtils.getTopShow(fm)
                    val targetFragment = MessageDetailFragment()

                    Bundle().apply {
                        putParcelable(AppKeys.KEY_target_user, intent?.getParcelableExtra(AppKeys.KEY_target_user))
                    }.also {
                        targetFragment.arguments = it
                        fm.beginTransaction()
                            .setCustomAnimations(
                                R.anim.abc_grow_fade_in_from_bottom,
                                R.anim.abc_fade_out,
                                R.anim.abc_fade_in,
                                R.anim.abc_shrink_fade_out_from_bottom
                            )
                            .add(R.id.fragment_container, targetFragment, targetFragment.javaClass.name)
                            .hide(topShowFragment)
                            .addToBackStack(targetFragment.javaClass.name)
                            .commitAllowingStateLoss()
                    }
                }
            }
        }
    }

    /**
     *  根据登录状态获取Fragment
     */
    fun appStartRoute(intent: Intent? = null, callback: (BaseFragment) -> Unit) {
        SPUtils.getInstance().getBoolean(AppKeys.SP_is_first_in, true).also { isFirstIn ->
            if (isFirstIn) {
                // 首次打开APP
                callback(StartFragment())
            } else {
                SPUtils.getInstance().getLong(AppKeys.SP_current_user_id, 0L).also { userId ->
                    if (userId > 0L) {
                        // 已登录
                        UserRepository.instance.getByAsync(userId).then { user ->
                            // 数据库异常
                            if (user == null) {
                                callback(SignInFragment())
                            } else {
                                setup(user)

                                when (intent?.action) {
                                    AppKeys.ACTION_new_message -> {
                                        val targetFragment = MessageDetailFragment()

                                        Bundle().apply {
                                            putParcelable(
                                                AppKeys.KEY_target_user,
                                                intent.getParcelableExtra(AppKeys.KEY_target_user)
                                            )
                                        }.also { args ->
                                            targetFragment.arguments = args
                                            callback(targetFragment)
                                        }
                                    }
                                    else -> {
                                        callback(MainFragment())
                                    }
                                }
                            }
                        }
                    } else {
                        // 未登录
                        callback(SignInFragment())
                    }
                }
            }
        }
    }

    /**
     * 设置登录用户信息
     */
    fun setup(user: User) {
        GlobalScope.launch {
            runOnMain {
                obUser.value = user
            }
            initHttpTask()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Utils.getApp().startForegroundService(Intent(Utils.getApp(), IMService::class.java))
            } else {
                Utils.getApp().startService(Intent(Utils.getApp(), IMService::class.java))
            }
        }
    }

    /**
     *  添加登录超时处理
     */
    private fun initHttpTask() {
        HttpWrapper.addHttpTask(CodeMap.ErrorTokenFailed, object : HttpTask {
            override fun handle() {
                SPUtils.getInstance().put(AppKeys.SP_current_user_id, 0L)
                ServiceUtils.stopService(IMService::class.java)
                ServiceUtils.stopService(IMGuardService::class.java)

                AppActivity.instance.let { activity ->
                    runOnMain {
                        MaterialAlertDialogBuilder(activity)
                            .setTitle(R.string.login_timeout)
                            .setMessage(R.string.login_first)
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
                                    .commitAllowingStateLoss()
                            }
                            .setPositiveButton(R.string.ok, null)
                            .show()
                    }
                }
            }
        })
    }

    /**
     *  被系统销毁时保存User
     */
    fun saveInstanceState(outState: Bundle) {
        outState.putParcelable("user", obUser.value)
    }

    /**
     *  恢复时读取User
     */
    fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let { bundle ->
            bundle.getParcelable<User>("user")?.let {
                obUser.value = it
            }
        }
    }

    fun initLifeSelf(activity: AppActivity) {
        // 注册生命周期事件
        activity.lifecycle.addObserver(
            @Suppress("unused")
            object : LifecycleObserver {

                @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                fun onCreate() {
                    AppActivity.instance = activity
                    AppActivity.appViewModel = ViewModelProviders.of(activity).get(AppViewModel::class.java)
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    activity.registerReceiver(newMessageReceiver, IntentFilter(AppKeys.ACTION_new_message))
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                fun onStop() {
                    activity.unregisterReceiver(newMessageReceiver)
                    // 取消网络任务
//                    HttpWrapper.cancelAll()
                }

//                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//                fun onDestroy() {
//                    AppActivity.instance = null
//                }
            })
    }

}