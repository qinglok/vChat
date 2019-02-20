package me.linx.vchat.app.data.model


import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ServiceUtils
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
import me.linx.vchat.app.ui.sign.SignInFragment
import me.linx.vchat.app.ui.start.StartFragment
import me.linx.vchat.app.utils.then
import me.linx.vchat.app.widget.base.BaseFragment


class AppViewModel : ObservableViewModel() {
    val obUser by lazy {
        MutableLiveData<User>()
    }

    /**
     *  根据登录状态获取Fragment
     */
    fun appStartRoute(action: (BaseFragment) -> Unit) {
        SPUtils.getInstance().getBoolean(AppKeys.SP_is_first_in, true).also { isFirstIn ->
            if (isFirstIn) {
                // 首次打开APP
                action(StartFragment())
            } else {
                SPUtils.getInstance().getLong(AppKeys.SP_current_user_id, 0L).also { userId ->
                    if (userId > 0L) {
                        // 已登录
                        UserRepository.instance.getByAsync(userId).then {
                            // 数据库异常
                            if (it == null) {
                                action(SignInFragment())
                            } else {
                                action(MainFragment())
//                                action(SignInFragment())
                                setup(it)
                            }
                        }
                    } else {
                        // 未登录
                        action(SignInFragment())
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
            launch(Dispatchers.Main) {
                obUser.value = user
            }
            initHttpTask()
            ServiceUtils.startService(IMService::class.java)
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

                AppActivity.instance?.let { activity ->
                    GlobalScope.launch(Dispatchers.Main) {
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
                                    .commit()
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

    fun onStop() {
        HttpWrapper.cancelAll()
    }

}