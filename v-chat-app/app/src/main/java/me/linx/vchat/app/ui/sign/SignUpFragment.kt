package me.linx.vchat.app.ui.sign

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import kotlinx.android.synthetic.main.toolbar_default.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.data.model.SignViewModel
import me.linx.vchat.app.databinding.FragmentSignUpBinding
import me.linx.vchat.app.utils.fitStatusBar
import me.linx.vchat.app.utils.hideSoftInput
import me.linx.vchat.app.utils.showSoftInput
import me.linx.vchat.app.widget.base.BaseFragment


class SignUpFragment : BaseFragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(mActivity).get(SignViewModel::class.java)
    }

    override fun setLayout() = R.layout.fragment_sign_up

    override fun initView(view: View, savedInstanceState: Bundle?) {
        DataBindingUtil.bind<FragmentSignUpBinding>(view)?.viewModel = viewModel

        view.toolbar.fitStatusBar()

        view.toolbar.setNavigationOnClickListener {
            view.hideSoftInput()
            fragmentManager?.popBackStack()
        }

        view.btn_sign_up.setOnClickListener { v ->
            viewModel.register(v, this)
        }

        // 邮箱一栏获取焦点并打开软键盘
        view.et_email.showSoftInput()
    }

}