package me.linx.vchat.app.widget.base

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_base.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.utils.fitStatusBar
import me.linx.vchat.app.utils.hideSoftInput

/**
 * linx 2018/9/24 20:35
 */
abstract class BaseFragment : Fragment() {
    lateinit var mActivity: AppCompatActivity
    lateinit var currentView : View
    private val toolBarConfig = ToolBarConfig()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base, container, false).also { baseView ->
            layoutInflater.inflate(setLayout(), container, false).apply {
                currentView = this
                initView(toolBarConfig, savedInstanceState)
                initActionBar(baseView.toolbar, toolBarConfig)
                baseView.fragment_container.addView(this)
            }
        }
    }

    private fun initActionBar(toolbar: Toolbar, toolBarConfig: ToolBarConfig) {
        with(toolBarConfig) {
            setHasOptionsMenu(toolBarConfig.menuRes != 0)

            if (showDefaultToolBar) {
                toolbar.visibility = View.VISIBLE
                toolbar.fitStatusBar()
                mActivity.setSupportActionBar(toolbar)
                mActivity.supportActionBar?.setTitle(titleRes)
                mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(enableBackOff)
                if (enableBackOff){
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (toolBarConfig.menuRes != 0) {
            inflater.inflate(toolBarConfig.menuRes, menu)
        }
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

    fun getPre(): BaseFragment? {
        val fragments = mActivity.supportFragmentManager.fragments
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