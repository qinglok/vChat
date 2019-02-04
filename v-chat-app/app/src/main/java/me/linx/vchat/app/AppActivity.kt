package me.linx.vchat.app

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import me.linx.vchat.app.data.model.UserViewModel

class AppActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val frameLayout = FrameLayout(this)
        frameLayout.id = R.id.fragment_container
        setContentView(frameLayout)

        //避免被强杀重启后重复load
        if (savedInstanceState == null) {
            viewModel.appStartRoute {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, it, it::class.java.name)
                    .commit()
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

