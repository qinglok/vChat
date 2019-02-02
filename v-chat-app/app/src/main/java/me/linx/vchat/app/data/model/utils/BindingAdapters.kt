package me.linx.vchat.app.data.model.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.utils.GlideApp
import me.linx.vchat.app.widget.GlideRoundTransform


object BindingAdapters {

    @BindingAdapter(value = ["roundImageUrl", "roundImageRadius"], requireAll = true)
    @JvmStatic
    fun ImageView.loadRoundImage(url: String?, radius: Int) {
        if (!url.isNullOrEmpty()) {
            GlideApp.with(context)
                .load(Api.baseFileDir + url)
                .transform(GlideRoundTransform(radius))
                .into(this)
        }
    }

}