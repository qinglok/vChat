package me.linx.vchat.app

import com.blankj.utilcode.util.SPUtils
import com.raizlabs.android.dbflow.kotlinextensions.list
import com.raizlabs.android.dbflow.kotlinextensions.select
import me.linx.vchat.app.common.base.BaseActivity
import me.linx.vchat.app.common.base.BaseFragment
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.db.entity.User
import me.linx.vchat.app.db.entity.User_Table
import me.linx.vchat.app.ec.main.MainFragment
import me.linx.vchat.app.ec.sign.SignParentFragment

class AppActivity : BaseActivity() {

    private var rootFragment: BaseFragment

    init {
        rootFragment = if (SPUtils.getInstance().getBoolean(AppKeys.isSignIn, false)) {
            val userId = SPUtils.getInstance().getLong(AppKeys.currentUserId, 0L)

            val result = select.from(User::class.java).where(User_Table.bizId.eq(userId)).list

            if (result.isEmpty()) {
                SignParentFragment()
            } else {
                MainFragment.create(result[0])
            }
        } else {
            SignParentFragment()
        }
    }

    override fun setRootFragment() = rootFragment

}