package me.linx.vchat.app.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.ui.main.me.MeFragment
import me.linx.vchat.app.ui.main.message.MessageFragment
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig

class MainFragment : BaseFragment() {
    private var currentPosition = 0

    override fun setLayout() = R.layout.fragment_main

    override fun initView(toolBarConfig: ToolBarConfig, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            currentPosition = it.getInt("currentPosition", 0)
        }
        arrayOf(
            MessageFragment(),
            PeopleFragment(),
            MeFragment()
        ).also { it ->
            currentView.apply {
                viewPager.apply {
                    offscreenPageLimit = 2
                    adapter = object : FragmentPagerAdapter(childFragmentManager) {
                        override fun getItem(position: Int): Fragment {
                            return it[position]
                        }

                        override fun getCount(): Int {
                            return it.size
                        }
                    }

                    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        }

                        override fun onPageSelected(position: Int) {
                            currentPosition = position
                            setNavigationItem(position, currentView.bottom_navigation)
                        }

                        override fun onPageScrollStateChanged(state: Int) {
                        }
                    })
                }

                bottom_navigation.setOnNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.item_message -> currentPosition = 0
                        R.id.item_people -> currentPosition = 1
                        R.id.item_shot -> currentPosition = 2
                    }
                    if (viewPager.currentItem != currentPosition) {
                        viewPager.currentItem = currentPosition
                    }
                    true
                }

                viewPager.currentItem = currentPosition
                setNavigationItem(currentPosition, bottom_navigation)
            }
        }
    }

    private fun setNavigationItem(position: Int, view: BottomNavigationView) {
        val selected = when (position) {
            1 -> R.id.item_people
            2 -> R.id.item_shot
            else -> R.id.item_message
        }

        if (view.selectedItemId != selected)
            view.selectedItemId = selected
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPosition", currentPosition)
    }

}