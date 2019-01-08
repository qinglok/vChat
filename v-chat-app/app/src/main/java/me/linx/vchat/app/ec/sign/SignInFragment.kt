package me.linx.vchat.app.ec.sign

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.raizlabs.android.dbflow.kotlinextensions.save
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.common.expand.snackbarError
import me.linx.vchat.app.common.expand.snackbarFailure
import me.linx.vchat.app.common.loader.LoaderDialogFragment
import me.linx.vchat.app.common.net.call
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.ec.main.MainFragment
import me.linx.vchat.app.net.Net
import org.jetbrains.anko.doAsync

internal class SignInFragment : AbsSignFragment() {

    override fun setLayout() = R.layout.fragment_sign_in

    override fun setTitle() = getString(R.string.sign_in)

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        rootView.btn_register.setOnClickListener {
            start(SignUpFragment())
        }

        rootView.btn_sign_in.setOnClickListener {
            hideSoftInput()
            val dialogFragment = LoaderDialogFragment()

            Net.service.signIn(
                et_email.text.toString(),
                et_password.text.toString(),
                DeviceUtils.getAndroidID()
            ).call {
                success { it ->
                    it.data?.let { user ->
                        doAsync {
                            user.save()
                            SPUtils.getInstance().put(AppKeys.isSignIn, true)
                            SPUtils.getInstance().put(AppKeys.currentUserId, user.bizId ?: 0L)
                        }

                        parentFragment().startWithPop(MainFragment.create(user))
                    }
                }
                start {
                    rootView.btn_sign_in.isEnabled = false
                    dialogFragment.show(fragmentManager!!, null)
                }
                finish {
                    dialogFragment.dismiss()
                    rootView.btn_sign_in.isEnabled = true
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