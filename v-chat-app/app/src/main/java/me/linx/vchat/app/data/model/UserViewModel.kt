package me.linx.vchat.app.data.model


import android.content.Intent
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.SPUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.linx.vchat.app.BR
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.model.utils.ObservableViewModel
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.net._OK
import me.linx.vchat.app.ui.main.HeadImageActivity
import me.linx.vchat.app.ui.sign.SignInFragment
import me.linx.vchat.app.utils.snackbarError
import me.linx.vchat.app.utils.snackbarFailure
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.loader.LoaderDialogFragment
import java.io.File


class UserViewModel : ObservableViewModel() {
    private val userRepository by lazy { UserRepository() }
    val obUser by lazy { ObservableUser() }

    /**
     *  根据登录状态跳转Fragment
     */
    fun appStartRoute(fromFragment: BaseFragment) {
        val fragmentId = fromFragment.id
        val fragmentManager = fromFragment.fragmentManager
        val userId = SPUtils.getInstance().getLong(AppKeys.currentUserId, 0L)

        if (userId > 0L) {
           GlobalScope.launch {
               startFragment(
//                   if (userRepository.getByAsync(userId).await() == null) SignInFragment() else MainFragment(),
                   if (userRepository.getByAsync(userId).await() == null) SignInFragment() else SignInFragment(),
                   fragmentManager,
                   fragmentId
               )
           }
        } else {
            startFragment(SignInFragment(), fragmentManager, fragmentId)
        }
    }

    /**
     *  跳转Fragment
     */
    private fun startFragment(fragment: BaseFragment, fragmentManager: FragmentManager?, fragmentId: Int) {
        fragmentManager?.beginTransaction()
            ?.replace(fragmentId, fragment)
            ?.commit()
    }

    /**
     * 设置登录用户信息
     */
    fun setup() {
        SPUtils.getInstance().getLong(AppKeys.currentUserId, 0L).also {
            GlobalScope.launch {
                userRepository.getByAsync(it).await()?.let {user ->
                    obUser.apply {
                        bizId = user.bizId ?: 0L
                        token = user.token ?: ""
                        nickName = user.nickName ?: ""
                        headImg = user.headImg ?: ""
                        // headImg = user?.headImg ?: "http://192.168.0.5:8080/aaa.jpg"
                    }
                }
            }
        }
    }

    /**
     *  查看头像大图
     */
    fun showBigImg(img: ImageView, f: BaseFragment) {
        if (obUser.headImg.isNotEmpty()) {
            val activity = f.mActivity
            val intent = Intent(activity, HeadImageActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                img,
                f.getString(R.string.photo_transition_name)
            )

            intent.putExtra(AppKeys.key_user_head_img, obUser.headImg)

            activity.startActivity(intent, options.toBundle())
        }
    }

    /**
     *  修改头像
     */
    fun newHeadImg(photoPath: String?, f: BaseFragment) {
        val file = File(photoPath)
        if (!file.exists()) {
            f.view.snackbarError(R.string.file_not_found)
            return
        }

        val loaderDialogFragment = LoaderDialogFragment()
        val rootView = f.view

        userRepository.postHeadImg(obUser.bizId, file) {
            success = { result ->
                if (result.code == _OK){
                    result.data?.let { path ->
                        GlobalScope.launch {
                            userRepository.getByAsync(obUser.bizId).await()?.apply {
                                headImg = path
                                userRepository.save(this)
                            }
                        }
                        obUser.headImg = path
                    }
                }else{
                    rootView.snackbarFailure(result.msg)
                }
            }
            start = {
                f.fragmentManager?.let { fm -> loaderDialogFragment.show(fm, null) }
            }
            finish = {
                loaderDialogFragment.dismiss()
            }
            error = {
                rootView.snackbarError(R.string.no_net)
            }
        }
    }

    /**
     *  双向绑定User Model
     */
    class ObservableUser : BaseObservable() {
        @get:Bindable
        var bizId: Long = 0L
            set(value) {
                field = value
                notifyPropertyChanged(BR.token)
            }

        @get:Bindable
        var token: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.token)
            }

        @get:Bindable
        var nickName: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.nickName)
            }

        @get:Bindable
        var headImg: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.headImg)
            }
    }
}