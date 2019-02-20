package me.linx.vchat.app.data.model.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.utils.GlideApp
import me.linx.vchat.app.widget.GlideRoundTransform
import java.text.SimpleDateFormat
import java.util.*


object BindingAdapters {

    @BindingAdapter(value = ["roundImageUrl", "roundImageRadius"], requireAll = true)
    @JvmStatic
    fun ImageView.loadRoundImage(url: String?, radius: Int) {
        if (!url.isNullOrEmpty()) {
            visibility = View.VISIBLE
            GlideApp.with(context)
                .load(Api.baseFileDir + url)
                .transform(GlideRoundTransform(radius))
                .into(this)
        }else{
            visibility = View.INVISIBLE
        }
    }

    @BindingAdapter(value = ["simpleFormat"])
    @JvmStatic
    fun TextView.simpleFormat(datetime : Long) {
        SimpleDateFormat.getDateTimeInstance().format(Date(datetime)).also {
            text = it
        }
    }

}