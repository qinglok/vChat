package me.linx.vchat.app.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import me.yokeyword.fragmentation.SwipeBackLayout
import me.yokeyword.fragmentation_swipeback.core.ISwipeBackFragment
import me.yokeyword.fragmentation_swipeback.core.SwipeBackFragmentDelegate
import kotlin.properties.Delegates

/**
 * linx 2018/9/24 20:55
 */
abstract class BaseSwipeBackFragment : BaseFragment(), ISwipeBackFragment {

    private var mDelegate : SwipeBackFragmentDelegate by Delegates.notNull()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setParallaxOffset(0.5f)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate = SwipeBackFragmentDelegate(this)
        mDelegate.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return attachToSwipeBack(createView(inflater, container, savedInstanceState))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDelegate.onViewCreated(view, savedInstanceState)
    }

    override fun attachToSwipeBack(view: View): View {
        return mDelegate.attachToSwipeBack(view)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mDelegate.onHiddenChanged(hidden)
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mDelegate.swipeBackLayout
    }

    /**
     * 是否可滑动
     *
     * @param enable
     */
    override fun setSwipeBackEnable(enable: Boolean) {
        mDelegate.setSwipeBackEnable(enable)
    }

    override fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel) {
        mDelegate.setEdgeLevel(edgeLevel)
    }

    override fun setEdgeLevel(widthPixel: Int) {
        mDelegate.setEdgeLevel(widthPixel)
    }

    /**
     * Set the offset of the parallax slip.
     */
    override fun setParallaxOffset(@FloatRange(from = 0.0, to = 1.0) offset: Float) {
        mDelegate.setParallaxOffset(offset)
    }

    override fun onDestroyView() {
        mDelegate.onDestroyView()
        super.onDestroyView()
    }

}