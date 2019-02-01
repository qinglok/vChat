package me.linx.vchat.app.data.model


import android.os.Bundle
import com.blankj.utilcode.util.SPUtils
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.model.utils.ObservableViewModel
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.net._OK
import me.linx.vchat.app.ui.main.MainFragment
import me.linx.vchat.app.ui.sign.SignInFragment
import me.linx.vchat.app.ui.start.StartFragment
import me.linx.vchat.app.utils.launch
import me.linx.vchat.app.utils.snackbarError
import me.linx.vchat.app.utils.snackbarFailure
import me.linx.vchat.app.utils.snackbarSuccess
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.loader.LoaderDialogFragment
import java.io.File


class UserViewModel : ObservableViewModel() {
    private val userRepository by lazy { UserRepository() }
    var obUser = User()

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
                        userRepository.getByAsync(userId).launch {
                            // 数据库异常
                            if (it == null) {
                                action(SignInFragment())
                            } else {
                                setup(it)
//                                action(MainFragment())
                                action(SignInFragment())
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
        obUser = user
    }

    /**
     *  修改头像
     */
    fun newHeadImg(photoPath: String?, f: BaseFragment) {
        val file = File(photoPath)
        if (!file.exists()) {
            f.view.snackbarError(R.string.file_not_found)
            return
        }

        val loaderDialogFragment = LoaderDialogFragment()
        val rootView = f.view

        userRepository.postHeadImg(obUser.bizId ?: 0L, file) {
            success = { result ->
                if (result.code == _OK) {
                    result.data?.let { path ->
                        userRepository.getByAsync(obUser.bizId ?: 0L).launch {
                            it?.apply {
                                headImg = path
                                userRepository.saveAsync(this).launch()
                            }
                            obUser.headImg = path
                        }
                    }
                } else {
                    rootView.snackbarFailure(result.msg)
                }
            }
            start = {
                f.fragmentManager?.let { fm -> loaderDialogFragment.show(fm, null) }
            }
            finish = {
                loaderDialogFragment.dismiss()
            }
            error = {
                rootView.snackbarError(R.string.no_net)
            }
        }
    }

    /**
     *  修改昵称
     */
    fun newNickName(name: String, f: BaseFragment) {
        if (name.isEmpty()) {
            f.view.snackbarFailure(f.getString(R.string.please_input_nick_name))
        } else {
            val loaderDialogFragment = LoaderDialogFragment()
            val rootView = f.view

            userRepository.postNickName(obUser.bizId ?: 0L, name) {
                success = { result ->
                    if (result.code == _OK) {
                        userRepository.getByAsync(obUser.bizId ?: 0L).launch {
                            it?.apply {
                                nickName = name
                                userRepository.saveAsync(this).launch()
                            }
                        }
                        obUser.nickName = name
                        result.msg?.let { rootView.snackbarSuccess(it) }
                    } else {
                        rootView.snackbarFailure(result.msg)
                    }
                }
                start = {
                    f.fragmentManager?.let { fm -> loaderDialogFragment.show(fm, null) }
                }
                finish = {
                    loaderDialogFragment.dismiss()
                }
                error = {
                    rootView.snackbarError(R.string.no_net)
                }
            }
        }
    }

    /**
     *  被系统销毁时保存User
     */
    fun saveInstanceState(outState: Bundle) {
        outState.putParcelable("user", obUser)
    }

    /**
     *  恢复时读取User
     */
    fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            savedInstanceState.getParcelable<User>("user")?.let {
                obUser = it
            }
        }
    }

}