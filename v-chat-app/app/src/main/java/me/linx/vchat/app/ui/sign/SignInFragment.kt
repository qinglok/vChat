package me.linx.vchat.app.ui.sign

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_sign_in.view.*
import kotlinx.android.synthetic.main.toolbar_default.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.data.model.SignViewModel
import me.linx.vchat.app.databinding.FragmentSignInBinding
import me.linx.vchat.app.utils.fitStatusBar
import me.linx.vchat.app.utils.showSoftInput
import me.linx.vchat.app.widget.base.BaseFragment

class SignInFragment : BaseFragment(), View.OnClickListener {
    private val viewModel by lazy {
        ViewModelProviders.of(mActivity).get(SignViewModel::class.java)
    }

    override fun setLayout() = R.layout.fragment_sign_in

    override fun initView(view: View, savedInstanceState: Bundle?) {
        DataBindingUtil.bind<FragmentSignInBinding>(view)?.viewModel = viewModel

        view.toolbar.fitStatusBar()

        view.btn_to_sign_up.setOnClickListener(this)
        view.btn_sign_in.setOnClickListener(this)

        // 邮箱一栏获取焦点并打开软键盘
        view.et_email.showSoftInput()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_to_sign_up -> {
                fragmentManager
                    ?.beginTransaction()
                    ?.setCustomAnimations(
                        R.anim.scale_alpha_in,
                        R.anim.alpha_out,
                        R.anim.alpha_in,
                        R.anim.scale_alpha_out
                    )
                    ?.replace(id, SignUpFragment())
                    ?.addToBackStack(SignUpFragment::class.java.name)
                    ?.commit()
            }
            R.id.btn_sign_in -> viewModel.login(v, this)
        }
    }

}