package me.linx.vchat.app.widget.base

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.BarUtils
import kotlinx.android.synthetic.main.fragment_base.view.*
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.R
import me.linx.vchat.app.utils.hideSoftInput

/**
 * linx 2018/9/24 20:35
 */
abstract class BaseFragment : Fragment() {
    lateinit var currentView: View

    private val toolBarConfig = ToolBarConfig()
    private var menu: Menu? = null
    private var menuInflater: MenuInflater? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base, container, false).also { baseView ->
            inflater.inflate(setLayout(), container, false).apply {
                currentView = this
                initView(toolBarConfig, savedInstanceState)
                initActionBar(baseView.toolbar, toolBarConfig)
                baseView.fragment_container.addView(this)
            }
        }
    }

    private fun initActionBar(toolbar: Toolbar, toolBarConfig: ToolBarConfig) {
        with(toolBarConfig) {
            setHasOptionsMenu(menuRes > 0)

            if (showDefaultToolBar) {
                toolbar.visibility = View.VISIBLE
                BarUtils.addMarginTopEqualStatusBarHeight(toolbar)
//                toolbar.fitStatusBar()
                AppActivity.instance.setSupportActionBar(toolbar)
                if (title.isNullOrEmpty()) {
                    AppActivity.instance.supportActionBar?.setTitle(titleRes)
                } else {
                    AppActivity.instance.supportActionBar?.setTitle(title)
                }
                AppActivity.instance.supportActionBar?.setDisplayHomeAsUpEnabled(enableBackOff)
                if (enableBackOff) {
                    toolbar.setNavigationOnClickListener {
                        onBackOffClick()
                    }
                }
                if (menuRes != 0) {
                    toolbar.setOnMenuItemClickListener(onMenuItemClick)
                }
            } else {
                toolbar.visibility = View.GONE
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && menu != null && menuInflater != null && toolBarConfig.menuRes > 0 && menu!!.size() == 0) {
            menuInflater!!.inflate(toolBarConfig.menuRes, menu)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (toolBarConfig.menuRes > 0 && menu.size() == 0) {
            inflater.inflate(toolBarConfig.menuRes, menu)
        }
        this.menu = menu
        this.menuInflater = inflater
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     *  设置引用布局
     */
    @LayoutRes
    abstract fun setLayout(): Int

    /**
     *  初始化View
     */
    abstract fun initView(toolBarConfig: ToolBarConfig, savedInstanceState: Bundle?)

    override fun onPause() {
        super.onPause()
        view?.hideSoftInput()
    }

    fun getParent() = parentFragment as? BaseFragment

    @Suppress("unused")
    fun getPre(): BaseFragment? {
        val fragments = AppActivity.instance.supportFragmentManager.fragments
        val index = fragments.indexOf(this)

        for (i in index - 1 downTo 0) {
            val fragment = fragments[i]
            if (fragment != null && fragment is BaseFragment) {
                return fragment
            }
        }
        return null
    }
}