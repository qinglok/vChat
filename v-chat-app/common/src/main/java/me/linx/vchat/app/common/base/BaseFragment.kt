package me.linx.vchat.app.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportFragment
import kotlin.properties.Delegates

/**
 * linx 2018/9/24 20:35
 */
abstract class BaseFragment : SupportFragment() {
    protected var rootView: View by Delegates.notNull()

    abstract fun setLayout(): Any

    abstract fun onBindView(savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createView(inflater, container, savedInstanceState)
    }

    fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = setLayout()
        rootView = when (layout) {
            is Int -> inflater.inflate(layout, container, false)
            is View -> layout
            else -> throw ClassCastException("setLayout()必须传入的是layout Id或者是一个View!")
        }
        onBindView(savedInstanceState)
        return rootView
    }

    fun preFragment() = super.getPreFragment() as BaseFragment

    fun parentFragment() = parentFragment as BaseFragment
}