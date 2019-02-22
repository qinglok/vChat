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
import me.linx.vchat.app.data.model.AppViewModel

class AppActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AppViewModel::class.java)
    }

    companion object {
        lateinit var instance : AppActivity
        lateinit var appViewModel : AppViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //启用透明状态栏
        BarUtils.setStatusBarColor(this, Color.argb(0, 0, 0, 0))
        //状态栏浅色模式
        BarUtils.setStatusBarLightMode(window, true)

        viewModel.initLifeSelf(this)

        FrameLayout(this).apply {
            id  = R.id.fragment_container
        }.also {
            setContentView(it)
        }

        //避免被强杀重启后重复load
        if (savedInstanceState == null) {
            viewModel.appStartRoute(intent) {
                GlobalScope.launch(Dispatchers.Main){
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, it, it::class.java.name)
                        .commitAllowingStateLoss()
                }
            }
        }
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

