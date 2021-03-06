package me.linx.vchat.app.ui.main

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_people.view.*
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.BR
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.constant.CodeMap
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.model.utils.DataBindingBaseQuickAdapter
import me.linx.vchat.app.data.model.utils.DataBindingBaseViewHolder
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.net.JsonResult
import me.linx.vchat.app.net.get
import me.linx.vchat.app.net.http
import me.linx.vchat.app.ui.main.message.MessageDetailFragment
import me.linx.vchat.app.utils.launch
import me.linx.vchat.app.utils.snackbarError
import me.linx.vchat.app.utils.then
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig


class PeopleFragment : BaseFragment() {

    override fun setLayout() = R.layout.fragment_people

    override fun initView(toolBarConfig: ToolBarConfig, savedInstanceState: Bundle?) {
        toolBarConfig.apply {
            showDefaultToolBar = true
            titleRes = R.string.people
        }

        val adapter = object : DataBindingBaseQuickAdapter<User>(R.layout.item_user) {
            override fun bind(helper: DataBindingBaseViewHolder?, item: User): HashMap<Int, Any> {
                return hashMapOf(BR.user to item)
            }
        }

        adapter.setOnItemClickListener { _, _, position ->
            val targetFragment = MessageDetailFragment()
            Bundle().apply {
                putParcelable(AppKeys.KEY_target_user, adapter.data[position])
            }.also {
                targetFragment.arguments = it

                getParent()?.also { parent ->
                    parent.fragmentManager
                        ?.beginTransaction()
                        ?.setCustomAnimations(
                            R.anim.abc_grow_fade_in_from_bottom,
                            R.anim.abc_fade_out,
                            R.anim.abc_fade_in,
                            R.anim.abc_shrink_fade_out_from_bottom
                        )
                        ?.add(parent.id, targetFragment, targetFragment.javaClass.name)
                        ?.hide(parent)
                        ?.addToBackStack(targetFragment.javaClass.name)
                        ?.commitAllowingStateLoss()
                }
            }
        }

        currentView.apply {
            this.rv.layoutManager = LinearLayoutManager(AppActivity.instance)
            adapter.bindToRecyclerView(this.rv)
            this.srl.setColorSchemeResources(R.color.color_primary)
            this.srl.setOnRefreshListener {
                updateUserInfo(this.srl, adapter)
            }
        }

        updateUserInfo(currentView.srl, adapter)
    }

    private fun updateUserInfo(srl: SwipeRefreshLayout, adapter: BaseQuickAdapter<User, DataBindingBaseViewHolder>) {
        SPUtils.getInstance().getLong(AppKeys.SP_current_user_id, 0L).also {
            UserRepository.instance.getByAsync(it).then { user ->
                user?.let {
                    Api.getActiveUserProfile.http()
                        .headers("token" to user.token)
                        .get<JsonResult<List<User>>> {
                            onSuccess = { result ->
                                if (result.code == CodeMap.Yes) {
                                    adapter.setNewData(result.data)
                                    UserRepository.instance.saveAsync(result.data).launch()
                                }
                            }
                            onStart = {
                                srl.isRefreshing = true
                            }
                            onFinish = {
                                srl.isRefreshing = false
                            }
                            onError = {
                                srl.snackbarError(R.string.no_net)
                            }
                        }
                }
            }
        }
    }

}