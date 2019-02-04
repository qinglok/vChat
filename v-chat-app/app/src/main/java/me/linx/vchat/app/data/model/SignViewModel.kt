package me.linx.vchat.app.data.model

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.SPUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.constant.CodeMap
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.ui.main.MainFragment
import me.linx.vchat.app.utils.hideSoftInput
import me.linx.vchat.app.utils.launch
import me.linx.vchat.app.utils.snackbarError
import me.linx.vchat.app.utils.snackbarFailure
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.loader.LoaderDialogFragment

class SignViewModel : ViewModel() {
    private var posting = false

    // 记录事件事件，防止频繁触发
    private var lastEventTime: Long = 0L
    // 最小触发间隔
    private val minEventTime = 1000L

    var obEmail = ObservableField<String>("")
    var obPassword = ObservableField<String>("")

    fun register(v: View, f: BaseFragment, secretQuestion: String, secretAnswer: String) {
        if (posting) {
            return
        }

        if (!checkEventTime()) {
            posting = false
            return
        }

        posting = true
        v.hideSoftInput()

        val loaderDialogFragment = LoaderDialogFragment()
        val rootView = f.view

        UserRepository.instance.register(obEmail.get(), obPassword.get(), secretQuestion, secretAnswer) {
            success = { result ->
                if (result.code == CodeMap.Yes) {
                    saveData(result.data, f)
                    startFragment(f, MainFragment())
                } else {
                    rootView?.snackbarFailure(result.msg)
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
                rootView?.snackbarError(R.string.no_net)
            }
        }
    }

    fun login(v: View, f: BaseFragment) {
        if (posting) {
            return
        }

        if (!checkEventTime()) {
            posting = false
            return
        }

        posting = true
        v.hideSoftInput()

        val loaderDialogFragment = LoaderDialogFragment()
        val rootView = f.view

        UserRepository.instance.login(obEmail.get(), obPassword.get()) {
            success = { result ->
                when (result.code) {
                    CodeMap.Yes -> {
                        saveData(result.data, f)
                        startFragment(f, MainFragment())
                    }
                    CodeMap.ErrorLoggedOther -> {
                        showVerifySecretDialog(result.msg, f)
                    }
                    else -> {
                        rootView?.snackbarFailure(result.msg)
                    }
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
                rootView?.snackbarError(R.string.no_net)
            }
        }
    }

    /**
     *  已经在其他设备登录，验证密保
     */
    private fun showVerifySecretDialog(msg: String?, f: BaseFragment) {
               msg?.let {
            var etSecretAnswer: EditText? = null
            val dialog = MaterialAlertDialogBuilder(f.context).apply {
                setView(R.layout.view_verify_secret)
                setTitle(R.string.verify_secret)
                setPositiveButton(R.string.ok) { _, _ ->
                    loginAndVerifySecret(etSecretAnswer?.text.toString(), f)
                }
                setNegativeButton(R.string.cancel, null)
            }.show()

            with(dialog) {
                findViewById<TextView>(R.id.tv_secret_question)?.text = msg
                etSecretAnswer = findViewById(R.id.et_secret_answer)
            }
        }
    }

    /**
     *  登录并验证密保
     */
    private fun loginAndVerifySecret(answer : String, f : BaseFragment){
        if (posting) {
            return
        }

        if (!checkEventTime()) {
            posting = false
            return
        }

        posting = true

        val loaderDialogFragment = LoaderDialogFragment()
        val rootView = f.view

        UserRepository.instance.loginAndVerifySecret(obEmail.get(), obPassword.get(), answer){
            success = { result ->
                when (result.code) {
                    CodeMap.Yes -> {
                        saveData(result.data, f)
                        startFragment(f, MainFragment())
                    }
                    else -> {
                        rootView?.snackbarFailure(result.msg)
                    }
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
                rootView?.snackbarError(R.string.no_net)
            }
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

    private fun saveData(user: User?, f: BaseFragment) =
        user?.apply {
            SPUtils.getInstance().put(AppKeys.SP_currentUserId, bizId ?: 0L)
            email = obEmail.get()
            UserRepository.instance.saveAsync(this).launch {
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
                            .replace(from.id, to, to::class.java.name)
                            .commit()
                    }
                }
            })
        }.start()

}