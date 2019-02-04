package me.linx.vchat.app.ui.sign

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.data.model.SignViewModel
import me.linx.vchat.app.databinding.FragmentSignUpBinding
import me.linx.vchat.app.utils.hideSoftInput
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig


class SignUpFragment : BaseFragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(SignViewModel::class.java)
    }

    override fun setLayout() = R.layout.fragment_sign_up

    override fun initView(view: View, savedInstanceState: Bundle?) {
        view.apply {
            DataBindingUtil.bind<FragmentSignUpBinding>(this)?.viewModel = viewModel

            btn_sign_up.setOnClickListener { v ->
                viewModel.register(v, this@SignUpFragment, et_secret_question.text.toString(), et_secret_answer.text.toString())
            }

            arguments?.let {
                viewModel.obEmail.set(it.getString("email", ""))
                viewModel.obPassword.set(it.getString("password", ""))
            }

            // 邮箱一栏获取焦点并打开软键盘
//            et_email.showSoftInput()
        }
    }

    override fun initToolBar(toolBarConfig: ToolBarConfig) {
        toolBarConfig.apply {
            showDefaultToolBar = true
            titleRes = R.string.sign_up
            enableBackOff = true
            onBackOffClick = {
                view?.hideSoftInput()
                fragmentManager?.popBackStack()
            }
        }
    }

}