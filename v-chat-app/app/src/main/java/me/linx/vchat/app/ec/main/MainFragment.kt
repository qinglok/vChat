package me.linx.vchat.app.ec.main

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.common.base.BaseFragment
import me.linx.vchat.app.common.expand.snackbar
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.db.entity.User

class MainFragment : BaseFragment() {
    private lateinit var user: User
    private var menuItem: MenuItem? = null

    companion object {
        fun create(user :User) : MainFragment{
            val fragment = MainFragment()
            val bundle = Bundle()

            bundle.putParcelable(AppKeys.key_user, user)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun setLayout() = R.layout.fragment_main

    override fun onBindView(savedInstanceState: Bundle?) {

        val fragments = arrayOf(
            MessageFragment(),
            PeopleFragment(),
            ShotFragment()
        )

        rootView.viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }
        }
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)

        rootView.bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_message -> viewPager.currentItem = 0
                R.id.item_people -> viewPager.currentItem = 1
                R.id.item_shot -> viewPager.currentItem = 2
            }
            false
        }

        rootView.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (menuItem != null) {
                    menuItem!!.isChecked = false
                } else {
                    rootView.bottom_navigation.menu.getItem(0).isChecked = false
                }
                menuItem = rootView.bottom_navigation.menu.getItem(position)
                menuItem!!.isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        test()
    }

    fun test(){
        arguments?.let {
            val u = it.getParcelable<User>(AppKeys.key_user)
            if (u == null){

            }else{
                user = u
                rootView.snackbar(user.email)
            }
        }
    }
}