package me.linx.vchat.app.ui.main.me

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import me.linx.vchat.app.R
import me.linx.vchat.app.data.model.FragmentMeViewModel
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig


class MeFragment : BaseFragment() {
    private val vm by lazy {
        ViewModelProviders.of(this).get(FragmentMeViewModel::class.java)
    }

    override fun setLayout() = R.layout.fragment_me

    override fun initView(toolBarConfig: ToolBarConfig, savedInstanceState: Bundle?) {
        vm.init(this, toolBarConfig)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vm.handelActivityResult(this, requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vm.handleSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        vm.handleViewStateRestored(savedInstanceState)
    }

}