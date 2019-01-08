package me.linx.vchat.app.ec.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragmnet_shot.view.*
import me.linx.vchat.app.R

class ShotFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragmnet_shot, container, false)

        view.btn_back.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
        }

        return view
    }
}