package me.linx.vchat.app.ec.start

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.R
import org.jetbrains.anko.startActivity

class StartActivity : AppCompatActivity(), SunAnimationView.AnimationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        sav_sun.startAnimation(this)

        btn_start.setOnClickListener {
            val animator = ObjectAnimator.ofFloat(v_mask, "alpha", 0f, 1f).setDuration(200)
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    sav_sun.end()

                    startActivity<AppActivity>()

                    finish()
                }
            })
            animator.start()
        }
    }

    override fun onAnimationComplete() {
        ObjectAnimator.ofFloat(btn_start, "alpha", 0f, 1f).setDuration(500).start()
    }
}