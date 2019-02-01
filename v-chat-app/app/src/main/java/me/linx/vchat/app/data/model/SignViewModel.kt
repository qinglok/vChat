package me.linx.vchat.app.data.model

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.SPUtils
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.net._OK
import me.linx.vchat.app.ui.main.MainFragment
import me.linx.vchat.app.utils.hideSoftInput
import me.linx.vchat.app.utils.launch
import me.linx.vchat.app.utils.snackbarError
import me.linx.vchat.app.utils.snackbarFailure
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.loader.LoaderDialogFragment

class SignViewModel : ViewModel() {
    private val userRepository by lazy { UserRepository() }
    private var posting = false

    // 记录事件事件，防止频繁触发
    private var lastEventTime: Long = 0L
    // 最小触发间隔
    private val minEventTime = 1000L

    var obEmail = ObservableField<String>("")
    var obPassword = ObservableField<String>("")

    fun login(v: View, f: BaseFragment) {
        if (!posting) {
            post(v, f, Api.login)
        }
    }

    fun register(v: View, f: BaseFragment) {
        if (!posting) {
            post(v, f, Api.register)
        }
    }

    private fun checkEventTime(): Boolean {
        return System.currentTimeMillis().let {
            (it - lastEventTime > minEventTime).also { isPass ->
                if (isPass) {
                    lastEventTime = it
                }
            }
        }
    }

    private fun post(v: View, f: BaseFragment, api: String) {
        if (!checkEventTime()) {
            posting = false
            return
        }

        posting = true
        v.hideSoftInput()

        val loaderDialogFragment = LoaderDialogFragment()
        val rootView = f.view

        userRepository.sign(api, obEmail.get(), obPassword.get()) {
            success = { result ->
                if (result.code == _OK) {
                    saveData(result.data, f)
                    startFragment(f, MainFragment())
                } else {
                    rootView.snackbarFailure(result.msg)
                }
            }
            start = {
                f.fragmentManager?.let { fm -> loaderDialogFragment.show(fm, null) }
            }
            finish = {
                loaderDialogFragment.dismiss()
                posting = false
            }
            error = {
                rootView.snackbarError(R.string.no_net)
            }
        }
    }

    private fun saveData(user: User?, f : BaseFragment) =
        user?.apply {
            SPUtils.getInstance().put(AppKeys.SP_currentUserId, bizId ?: 0L)
            email = obEmail.get()
            userRepository.saveAsync(this).launch {
                ViewModelProviders.of(f.mActivity).get(UserViewModel::class.java).setup(this)
            }
        }

    private fun startFragment(from: BaseFragment, to: BaseFragment) =
        ObjectAnimator.ofFloat(from.view, "alpha", 1f, 0f).apply {
            duration = 300
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    from.fragmentManager?.let { fm ->
                        if (fm.backStackEntryCount > 0) {
                            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }

                        fm.beginTransaction()
                            .replace(from.id, to)
                            .commit()
                    }
                }
            })
        }.start()

}