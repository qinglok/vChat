package me.linx.vchat.app.widget.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import me.linx.vchat.app.utils.hideSoftInput

/**
 * linx 2018/9/24 20:35
 */
abstract class BaseFragment : Fragment() {
    lateinit var mActivity: AppCompatActivity
    private lateinit var rootView: View

    @LayoutRes
    abstract fun setLayout(): Int

    abstract fun initView(view: View, savedInstanceState: Bundle?)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(setLayout(), container, false)
        initView(rootView, savedInstanceState)
        return rootView
    }

    override fun getView(): View {
        return rootView
    }

    override fun onPause() {
        super.onPause()
        rootView.hideSoftInput()
    }

    fun getParent() = parentFragment as? BaseFragment

    @Suppress("unused")
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