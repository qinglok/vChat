package me.linx.vchat.app.common.base

import android.os.Bundle
import android.widget.FrameLayout
import me.linx.vchat.app.common.R
import me.yokeyword.fragmentation.SupportActivity

/**
 * linx 2018/9/24 20:34
 */
abstract class BaseActivity : SupportActivity() {

    /**
     * 设置根Fragment
     */
    abstract fun setRootFragment(): BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //实例化一个容器container
        val container = FrameLayout(this)
        container.id = R.id.fragment_container
        setContentView(container)

        //避免被强杀重启后重复load
        if (savedInstanceState == null) {
            //把根Fragment加载到container
            loadRootFragment(R.id.fragment_container, setRootFragment())
        }
    }

}