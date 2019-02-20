package me.linx.vchat.app

import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.BarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.model.AppViewModel
import me.linx.vchat.app.ui.main.message.MessageDetailFragment
import me.linx.vchat.app.widget.base.BaseFragment

class AppActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AppViewModel::class.java)
    }

    companion object {
        var instance : AppActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //启用透明状态栏
        BarUtils.setStatusBarColor(this, Color.argb(0, 0, 0, 0))
        BarUtils.setStatusBarLightMode(window, true)

        FrameLayout(this).apply {
            id  = R.id.fragment_container
        }.also {
            setContentView(it)
        }

        var targetFragment : BaseFragment? = null
        val action = intent.getIntExtra(AppKeys.ACTION_app_activity, 0)
        when(action){
            AppKeys.ACTION_VALUE_new_message ->{
                targetFragment = MessageDetailFragment()

                Bundle().apply {
                    putParcelable(AppKeys.KEY_target_user, intent.getParcelableExtra(AppKeys.KEY_target_user))
                }.also {
                    targetFragment.arguments = it
                }
            }
        }

        //避免被强杀重启后重复load
        if (savedInstanceState == null) {
            if (targetFragment!= null){
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, targetFragment, targetFragment::class.java.name)
                    .commit()
            }else{
                viewModel.appStartRoute {
                    GlobalScope.launch(Dispatchers.Main){
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container, it, it::class.java.name)
                            .commit()
                    }
                }
            }
        }

        instance = this
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.restoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
//            moveTaskToBack(true)
            finish()
        }
    }

}

