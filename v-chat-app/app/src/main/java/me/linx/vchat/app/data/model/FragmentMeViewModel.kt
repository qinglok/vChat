package me.linx.vchat.app.data.model

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ServiceUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import me.linx.vchat.app.AppActivity
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.constant.CodeMap
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.data.im.IMGuardService
import me.linx.vchat.app.data.im.IMService
import me.linx.vchat.app.data.repository.UserRepository
import me.linx.vchat.app.databinding.FragmentMeBinding
import me.linx.vchat.app.net.JsonResult
import me.linx.vchat.app.net.http
import me.linx.vchat.app.net.post
import me.linx.vchat.app.ui.main.me.AvatarActivity
import me.linx.vchat.app.ui.main.me.MeFragment
import me.linx.vchat.app.ui.sign.SignInFragment
import me.linx.vchat.app.utils.launch
import me.linx.vchat.app.utils.showOrHideSoftInput
import me.linx.vchat.app.utils.snackbarError
import me.linx.vchat.app.utils.snackbarFailure
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FragmentMeViewModel : ViewModel() {
    lateinit var obUser: User

    private val requestTakePhotoFromCollection = 1
    private val requestTakePhotoFromCamera = 2
    // 临时文件的绝对路径
    private var mCurrentPhotoPath: String? = null

    fun init(f: MeFragment, toolBarConfig: ToolBarConfig) {
        AppActivity.appViewModel.obUser.observeForever {
            obUser = it
        }

        val viewClick by lazy {
            View.OnClickListener { v ->
                when (v?.id) {
                    R.id.iv_avatar -> {
                        showBigImg(v, f)
                    }
                    R.id.gl_avatar -> {
                        showOptionsDialog(f)
                    }
                    R.id.gl_nickname -> {
                        showNewNameDialog(f)
                    }
                }
            }
        }

        val menuItemClick by lazy {
            Toolbar.OnMenuItemClickListener { item ->
                item?.itemId?.let {
                    when (it) {
                        R.id.logout -> {
                            f.getParent()?.let { parent ->
                                logout(parent)
                            }
                            true
                        }
                        R.id.login_timeout_test -> {
                            loginTimeoutTest()
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
                false
            }
        }

        DataBindingUtil.bind<FragmentMeBinding>(f.currentView)?.apply {
            viewModel = this@FragmentMeViewModel
            ivAvatar.transitionName = f.getString(R.string.photo_transition_name)
            ivAvatar.setOnClickListener(viewClick)
            glAvatar.setOnClickListener(viewClick)
            glNickname.setOnClickListener(viewClick)
            srl.setColorSchemeResources(R.color.color_primary)
            srl.setOnRefreshListener {
                updateUserInfo(srl)
            }
        }

        toolBarConfig.apply {
            showDefaultToolBar = true
            titleRes = R.string.me
            menuRes = R.menu.menu_me_options
            onMenuItemClick = menuItemClick
        }
    }

    /**
     *  查看头像大图
     */
    private fun showBigImg(iv_avatar: View, f: BaseFragment) {
        obUser.avatar?.let {
            if (it.isNotEmpty()) {
                val intent = Intent(f.context, AvatarActivity::class.java)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    AppActivity.instance,
                    iv_avatar,
                    f.getString(R.string.photo_transition_name)
                )

                intent.putExtra(AppKeys.KEY_user_avatar, obUser.avatar)

                f.startActivity(intent, options.toBundle())
            }
        }
    }

    /**
     *  显示选择修改头像方式Dialog
     */
    private fun showOptionsDialog(f: BaseFragment) {
        arrayOf<CharSequence>("相册", "拍照").also {
            MaterialAlertDialogBuilder(f.context)
                .setTitle(R.string.edit_avatar)
                .setItems(
                    it
                ) { _, which ->
                    when (which) {
                        0 -> takeFromPhotoCollection(f)
                        1 -> dispatchTakePictureIntent(f)
                    }
                }
                .setIcon(R.drawable.ic_sentiment_satisfied_black_24dp)
                .show()
        }
    }

    /**
     *  显示修改昵称Dialog
     */
    private fun showNewNameDialog(f: BaseFragment) {
        var editText: EditText? = null
        editText = MaterialAlertDialogBuilder(f.context).apply {
            setView(R.layout.view_edit_nickname)
            setTitle(R.string.edit_nickname)
            setPositiveButton(
                R.string.save
            ) { _, _ ->
                newNickname(editText?.text.toString(), f)
            }
            setNegativeButton(R.string.cancel, null)
        }.show().findViewById<EditText>(R.id.et_name).apply {
            this?.setText(obUser.nickname)
            this?.setSelection(obUser.nickname?.length ?: 0)
            this?.showOrHideSoftInput()
        }
    }

    /**
     *  从相册选择图片
     */
    private fun takeFromPhotoCollection(f: BaseFragment) {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // 单选
            putExtra(Intent.EXTRA_LOCAL_ONLY, true) // 只限本地
        }.let { intent ->
            intent.resolveActivity(AppActivity.instance.packageManager)?.let {
                f.startActivityForResult(intent, requestTakePhotoFromCollection)
            }
        }
    }

    /**
     *  拍照
     */
    private fun dispatchTakePictureIntent(f: BaseFragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { intent ->
            // Ensure that there's a camera activity to handle the intent
            intent.resolveActivity(AppActivity.instance.packageManager)?.let {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    f.currentView.snackbarError(R.string.sys_error)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.let { file ->
                    val photoURI: Uri = FileProvider.getUriForFile(
                        AppActivity.instance,
                        AppUtils.getAppPackageName() + ".provider",
                        file
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    f.startActivityForResult(intent, requestTakePhotoFromCamera)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val cachePath = PathUtils.getExternalAppPicturesPath()
        val storageDir = File(cachePath)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    fun handelActivityResult(f: BaseFragment, requestCode: Int, resultCode: Int, data: Intent?) {
        // 裁剪失败
        if (resultCode == UCrop.RESULT_ERROR) {
//            val cropError = UCrop.getError(data!!)
            f.currentView.snackbarError(R.string.sys_error)
        }

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            // 从拍照返回
            requestTakePhotoFromCamera -> {
                toCrop(f, Uri.fromFile(File(mCurrentPhotoPath)))
            }
            // 从相册返回
            requestTakePhotoFromCollection -> {
                data?.data?.let { sourceUri ->
                    toCrop(f, sourceUri)
                }
            }
            // 裁剪完毕
            UCrop.REQUEST_CROP -> {
                newAvatar(mCurrentPhotoPath, f)
            }
        }
    }

    /**
     *  裁剪图片
     */
    private fun toCrop(f: BaseFragment, sourceUri: Uri) {
        val tempFile = try {
            createImageFile()
        } catch (ex: IOException) {
            f.currentView.snackbarError(R.string.sys_error)
            null
        }

        // 裁剪后的输出目标
        Uri.fromFile(tempFile).let { destinationUri ->
            // UCrop Activity设置
            UCrop.Options().apply {
                setToolbarColor(Color.WHITE)
                setStatusBarColor(Color.TRANSPARENT)
                setToolbarWidgetColor(Color.BLACK)
                setHideBottomControls(true)
            }.let {
                // 跳转到UCrop Activity
                UCrop.of(sourceUri, destinationUri)
                    .withOptions(it)
                    .withAspectRatio(1f, 1f)
                    .start(AppActivity.instance, f)
            }
        }
    }

    /**
     *  修改头像
     */
    private fun newAvatar(photoPath: String?, f: BaseFragment) {
        val file = File(photoPath)
        if (!file.exists()) {
            f.view?.snackbarError(R.string.file_not_found)
            return
        }

        val userRepository = UserRepository.instance

        userRepository.postAvatar(obUser, file) {
            withLoader = true
            onSuccess = { result ->
                if (result.code == CodeMap.Yes) {
                    result.data?.let { path ->
                        obUser.apply {
                            avatar = path
                            userRepository.saveAsync(this).launch()
                        }
                    }
                } else {
                    f.view?.snackbarFailure(result.msg)
                }
            }
            onError = {
                f.view?.snackbarError(R.string.no_net)
            }
        }
    }

    /**
     *  修改昵称
     */
    private fun newNickname(name: String, f: BaseFragment) {
        if (name.isEmpty()) {
            f.view?.snackbarFailure(f.getString(R.string.please_input_nickname))
        } else {
            val userRepository = UserRepository.instance

            userRepository.postNickname(obUser, name) {
                withLoader = true
                onSuccess = { result ->
                    if (result.code == CodeMap.Yes) {
                        obUser.apply {
                            nickname = name
                            userRepository.saveAsync(this).launch()
                        }
                    } else {
                        f.view?.snackbarFailure(result.msg)
                    }
                }
                onError = {
                    f.view?.snackbarError(R.string.no_net)
                }
            }
        }
    }

    /**
     *  退出登录
     */
    private fun logout(f: BaseFragment) {
        UserRepository.instance.logout(obUser) {
            withLoader = true
            onSuccess = { result ->
                if (result.code == CodeMap.Yes) {
                    SPUtils.getInstance().put(AppKeys.SP_current_user_id, 0L)
                    ServiceUtils.stopService(IMService::class.java)
                    ServiceUtils.stopService(IMGuardService::class.java)

                    f.fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    f.fragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, SignInFragment(), SignInFragment::class.java.name)
                        ?.commitAllowingStateLoss()
                } else {
                    f.view?.snackbarFailure(result.msg)
                }
            }
            onError = {
                f.view?.snackbarError(R.string.no_net)
            }
        }
    }

    /**
     *  更新用户信息
     */
    private fun updateUserInfo(srl: SwipeRefreshLayout) {
        UserRepository.instance.getUserProfile(obUser.token ?: "", obUser.updateTime) {
            onSuccess = { result ->
                if (result.code == CodeMap.Yes) {
                    result.data?.let { info ->
                        obUser.nickname = info.nickname
                        obUser.avatar = info.avatar
                        obUser.updateTime = info.updateTime
                        UserRepository.instance.saveAsync(obUser).launch()
                    }
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

    /**
     *  登录超时测试
     */
    private fun loginTimeoutTest() {
        Api.loginTimeoutTest.http().post<JsonResult<Unit>> { }
    }

    fun handleSaveInstanceState(outState: Bundle) {
        outState.putString("mCurrentPhotoPath", mCurrentPhotoPath)
    }

    fun handleViewStateRestored(savedInstanceState: Bundle?) {
        mCurrentPhotoPath = savedInstanceState?.getString("mCurrentPhotoPath")
    }
}