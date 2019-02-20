package me.linx.vchat.app.ui.sign

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_sign_in.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.data.model.FragmentSignViewModel
import me.linx.vchat.app.databinding.FragmentSignInBinding
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig

class SignInFragment : BaseFragment(), View.OnClickListener {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(FragmentSignViewModel::class.java)
    }

    override fun setLayout() = R.layout.fragment_sign_in

    override fun initView(toolBarConfig: ToolBarConfig, savedInstanceState: Bundle?) {
        currentView.apply {
            DataBindingUtil.bind<FragmentSignInBinding>(this)?.viewModel = viewModel

            btn_to_sign_up.setOnClickListener(this@SignInFragment)
            btn_sign_in.setOnClickListener(this@SignInFragment)

            // 邮箱一栏获取焦点并打开软键盘
//            et_email.showSoftInput()
        }

        toolBarConfig.apply {
            showDefaultToolBar = true
            titleRes = R.string.sign_in
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_to_sign_up -> {
                val signUpFragment = SignUpFragment()
                val args = Bundle().apply {
                    putString("email", viewModel.obEmail.get())
                    putString("password", viewModel.obPassword.get())
                }
                signUpFragment.arguments = args

                fragmentManager
                    ?.beginTransaction()
                    ?.setCustomAnimations(
                        R.anim.abc_grow_fade_in_from_bottom,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_shrink_fade_out_from_bottom
                    )
                    ?.replace(id, signUpFragment, signUpFragment.javaClass.name)
                    ?.addToBackStack(signUpFragment.javaClass.name)
                    ?.commitAllowingStateLoss()
            }
            R.id.btn_sign_in -> viewModel.login(v, this)
        }
    }

}