package me.linx.vchat.app.data.model.utils

import android.view.View
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseViewHolder
import me.linx.vchat.app.R

class DataBindingBaseViewHolder(view : View) : BaseViewHolder(view) {

    fun getBinding(): ViewDataBinding {
        return itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as ViewDataBinding
    }
}