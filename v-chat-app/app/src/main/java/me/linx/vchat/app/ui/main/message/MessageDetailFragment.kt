package me.linx.vchat.app.ui.main.message

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import me.linx.vchat.app.R
import me.linx.vchat.app.data.model.FragmentMessageDetailViewModel
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig

class MessageDetailFragment : BaseFragment() {
    private val vm by lazy { ViewModelProviders.of(this).get<FragmentMessageDetailViewModel>() }

    override fun setLayout() = R.layout.fragment_message_detail

    override fun initView(toolBarConfig: ToolBarConfig, savedInstanceState: Bundle?) {
        vm.init(this, toolBarConfig)
    }
}