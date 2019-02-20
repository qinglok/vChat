package me.linx.vchat.app.data.model.utils

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import me.linx.vchat.app.R

abstract class DataBindingBaseQuickAdapter<T>(layout : Int) : BaseQuickAdapter<T, DataBindingBaseViewHolder>(layout) {

    override fun convert(helper: DataBindingBaseViewHolder?, item: T) {
        val binding = helper!!.getBinding()
        bind(helper, item).forEach {
            binding.setVariable(it.key, it.value)
        }
        binding.executePendingBindings()
    }

    abstract fun bind(helper: DataBindingBaseViewHolder?, item: T) : HashMap<Int, Any>

    fun getRecycler(): RecyclerView {
        return super.getRecyclerView()
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup?): View {
        val binding: ViewDataBinding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false)
        val view = binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }
}