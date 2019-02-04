package me.linx.vchat.app.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_main.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.widget.base.BaseFragment

class MainFragment : BaseFragment() {
    private var menuItem: MenuItem? = null

    override fun setLayout() = R.layout.fragment_main

    override fun initView(view: View, savedInstanceState: Bundle?) {
        arrayOf(
            MessageFragment(),
            PeopleFragment(),
            MeFragment()
        ).also {
            view.apply {
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
                            if (menuItem != null) {
                                menuItem!!.isChecked = false
                            } else {
                                view.bottom_navigation.menu.getItem(0).isChecked = false
                            }
                            menuItem = view.bottom_navigation.menu.getItem(position)
                            menuItem!!.isChecked = true
                        }

                        override fun onPageScrollStateChanged(state: Int) {
                        }
                    })
                }

                bottom_navigation.setOnNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.item_message -> viewPager.currentItem = 0
                        R.id.item_people -> viewPager.currentItem = 1
                        R.id.item_shot -> viewPager.currentItem = 2
                    }
                    false
                }
            }
        }
    }
}