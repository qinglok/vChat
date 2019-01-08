package me.linx.vchat.app.ec.sign

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import me.linx.vchat.app.R
import me.linx.vchat.app.common.base.BaseFragment

internal abstract class AbsSignFragment : BaseFragment() {

    override fun onBindView(savedInstanceState: Bundle?) {
    }

    abstract fun setTitle() : String

    override fun onSupportVisible() {
        super.onSupportVisible()
        parentFragment?.let {
            ViewModelProviders.of(it).get(ToolbarViewMode::class.java).currentTitle.value = setTitle()
        }
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        //清除外容器touch事件
        rootView.findViewById<View>(R.id.root).setOnTouchListener { _, _ ->
            true
        }
        post { showSoftInput(rootView.findViewById(R.id.et_email)) }
    }
}