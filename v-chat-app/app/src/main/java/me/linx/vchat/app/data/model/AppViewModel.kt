package me.linx.vchat.app.data.model


import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.SPUtils
import kotlinx.coroutines.Dispatchers
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.model.utils.ObservableViewModel
import me.linx.vchat.app.data.repository.UserRepository
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
                SPUtils.getInstance().getLong(AppKeys.SP_currentUserId, 0L).also { userId ->
                    if (userId > 0L) {
                        // 已登录
                        UserRepository.instance.getByAsync(userId).then(Dispatchers.Main) {
                            // 数据库异常
                            if (it == null) {
                                action(SignInFragment())
                            } else {
                                setup(it)
                                action(MainFragment())
//                                action(SignInFragment())
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
        obUser.value = user
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

}