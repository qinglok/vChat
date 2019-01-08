package me.linx.vchat.app.ec.sign

import android.os.Bundle
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.raizlabs.android.dbflow.kotlinextensions.save
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.common.expand.snackbarError
import me.linx.vchat.app.common.expand.snackbarFailure
import me.linx.vchat.app.common.loader.LoaderDialogFragment
import me.linx.vchat.app.common.net.call
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.ec.main.MainFragment
import me.linx.vchat.app.net.Net
import org.jetbrains.anko.doAsync


internal class SignUpFragment : AbsSignFragment() {

    override fun setLayout() = R.layout.fragment_sign_up

    override fun setTitle() = getString(R.string.sign_up)

    override fun onBackPressedSupport(): Boolean {
        pop()
        return true
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)

        rootView.btn_sign_up.setOnClickListener {
            hideSoftInput()
            val dialogFragment = LoaderDialogFragment()

            Net.service
                .signUp(
                    rootView.et_email.text.toString(),
                    rootView.et_password.text.toString(),
                    DeviceUtils.getAndroidID()
                ).call {
                    success { it ->
                        it.data?.let { user ->
                            doAsync {
                                user.email = rootView.et_email.text.toString()
                                user.save()
                                SPUtils.getInstance().put(AppKeys.isSignIn, true)
                                SPUtils.getInstance().put(AppKeys.currentUserId, user.bizId ?: 0L)
                            }
                            parentFragment().startWithPop(MainFragment.create(user))
                        }
                    }
                    start {
                        rootView.btn_sign_up.isEnabled = false
                        dialogFragment.show(fragmentManager!!, null)
                    }
                    finish {
                        dialogFragment.dismiss()
                        rootView.btn_sign_up.isEnabled = true
                    }
                    failure { _, s ->
                        rootView.snackbarFailure(s)
                    }
                    error {
                        rootView.snackbarError(R.string.no_net)
                    }
                }
        }
    }

}