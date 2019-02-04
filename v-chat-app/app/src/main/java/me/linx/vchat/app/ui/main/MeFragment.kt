package me.linx.vchat.app.ui.main

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.*
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.fragment_me.view.*
import me.linx.vchat.app.R
import me.linx.vchat.app.constant.AppKeys
import me.linx.vchat.app.data.model.UserViewModel
import me.linx.vchat.app.databinding.FragmentMeBinding
import me.linx.vchat.app.utils.showOrHideSoftInput
import me.linx.vchat.app.utils.snackbarError
import me.linx.vchat.app.widget.base.BaseFragment
import me.linx.vchat.app.widget.base.ToolBarConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MeFragment : BaseFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private val viewModel by lazy {
        ViewModelProviders.of(mActivity).get(UserViewModel::class.java)
    }
    private val requestTakePhotoFromCollection = 1
    private val requestTakePhotoFromCamera = 2
    // 临时文件的绝对路径
    private var mCurrentPhotoPath: String? = null

    override fun setLayout() = R.layout.fragment_me

    override fun initView(view: View, savedInstanceState: Bundle?) {
        view.apply {
            DataBindingUtil.bind<FragmentMeBinding>(this)?.viewModel = viewModel

            iv_head_img.transitionName = getString(R.string.photo_transition_name)

            iv_head_img.setOnClickListener(this@MeFragment)
            gl_head_img.setOnClickListener(this@MeFragment)
            gl_nick_name.setOnClickListener(this@MeFragment)
        }
    }

    override fun initToolBar(toolBarConfig: ToolBarConfig) {
        toolBarConfig.apply {
            showDefaultToolBar = true
            titleRes = R.string.me_cn
            menuRes = R.menu.menu_me_options
            onMenuItemClick = this@MeFragment
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser && !isDetached && isAdded){
            viewModel.updateUserInfo()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.itemId?.let {
            when (it) {
                R.id.logout -> {
                    getParent()?.let { parent ->
                        viewModel.logout(parent)
                    }
                    return true
                }
                R.id.login_timeout_test -> {
                    viewModel.loginTimeoutTest()
                }

                else -> {
                    return false
                }
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_head_img -> {
                showBigImg()
            }
            R.id.gl_head_img -> {
                showOptionsDialog()
            }
            R.id.gl_nick_name -> {
                showNewNameDialog()
            }
        }
    }

    private fun showNewNameDialog() {
        var editText: EditText? = null
        editText = MaterialAlertDialogBuilder(context).apply {
            setView(R.layout.view_edit_nick_name)
            setTitle(R.string.edit_nick_name)
            setPositiveButton(
                R.string.save
            ) { _, _ ->
                viewModel.newNickName(editText?.text.toString(), this@MeFragment)
            }
            setNegativeButton(R.string.cancel, null)
        }.show().findViewById<EditText>(R.id.et_name).apply {
            this?.setText(viewModel.obUser.nickName)
            this?.showOrHideSoftInput()
        }
    }

    /**
     *  查看头像大图
     */
    private fun showBigImg() {
        viewModel.obUser.headImg?.let {
            if (it.isNotEmpty()) {
                val intent = Intent(mActivity, HeadImageActivity::class.java)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    mActivity,
                    iv_head_img,
                    getString(R.string.photo_transition_name)
                )

                intent.putExtra(AppKeys.KEY_user_head_img, viewModel.obUser.headImg)

                startActivity(intent, options.toBundle())
            }
        }
    }

    /**
     *  Dialog选择修改头像方式
     */
    private fun showOptionsDialog() = arrayOf<CharSequence>("相册", "拍照").also {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.edit_head_img)
            .setItems(
                it
            ) { _, which ->
                when (which) {
                    0 -> takeFromPhotoCollection()
                    1 -> dispatchTakePictureIntent()
                }
            }
            .setIcon(R.drawable.ic_sentiment_satisfied_black_24dp)
            .show()
    }


    /**
     *  创建临时文件
     */
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

    /**
     *  从相册选择图片
     */
    private fun takeFromPhotoCollection() {
        Intent(ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(EXTRA_ALLOW_MULTIPLE, false) // 单选
            putExtra(EXTRA_LOCAL_ONLY, true) // 只限本地
        }.let { intent ->
            intent.resolveActivity(mActivity.packageManager)?.let {
                startActivityForResult(intent, requestTakePhotoFromCollection)
            }
        }
    }

    /**
     *  拍照
     */
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { intent ->
            // Ensure that there's a camera activity to handle the intent
            intent.resolveActivity(mActivity.packageManager)?.let {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    view?.snackbarError(R.string.sys_error)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.let { file ->
                    val photoURI: Uri = FileProvider.getUriForFile(
                        Utils.getApp(),
                        AppUtils.getAppPackageName() + ".provider",
                        file
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, requestTakePhotoFromCamera)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 裁剪失败
        if (resultCode == UCrop.RESULT_ERROR) {
//            val cropError = UCrop.getError(data!!)
            view?.snackbarError(R.string.sys_error)
        }

        if (resultCode != RESULT_OK) return

        when (requestCode) {
            // 从拍照返回
            requestTakePhotoFromCamera -> {
                toCrop(Uri.fromFile(File(mCurrentPhotoPath)))
                LogUtils.d(data.toString())
            }
            // 从相册返回
            requestTakePhotoFromCollection -> {
                data?.data?.let { sourceUri ->
                    toCrop(sourceUri)
                }
            }
            // 裁剪完毕
            UCrop.REQUEST_CROP -> {
                viewModel.newHeadImg(mCurrentPhotoPath, this)
            }
        }
    }

    /**
     *  裁剪图片
     */
    private fun toCrop(sourceUri: Uri) {
        val tempFile = try {
            createImageFile()
        } catch (ex: IOException) {
            view?.snackbarError(R.string.sys_error)
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
                    .start(mActivity, this)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("mCurrentPhotoPath", mCurrentPhotoPath)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mCurrentPhotoPath = savedInstanceState?.getString("mCurrentPhotoPath")
    }

}