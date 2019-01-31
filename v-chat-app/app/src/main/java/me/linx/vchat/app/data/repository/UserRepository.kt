package me.linx.vchat.app.data.repository

import com.blankj.utilcode.util.DeviceUtils
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.api.UploadAction
import me.linx.vchat.app.data.db.AppDatabase
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.net.HttpCallback
import me.linx.vchat.app.net.JsonResult
import me.linx.vchat.app.net.http
import me.linx.vchat.app.net.post
import me.linx.vchat.app.utils.rxRun
import java.io.File

class UserRepository {
    private val userDao by lazy { AppDatabase.db.userDao() }

    fun save(user: User) = rxRun {
        userDao.insert(user)
    }

    fun getBy(userId: Long, success: (User?) -> Unit) = userDao.findByBizId(userId).rxRun(success)

    fun sign(api: String, email: String?, password: String?, init: HttpCallback<JsonResult<User>>.() -> Unit) =
        api.http()
            .params(
                "email" to email,
                "password" to password,
                "deviceId" to DeviceUtils.getAndroidID()
            )
            .post(init)

    fun postNickName(userId: Long, name: String?, init: HttpCallback<JsonResult<String>>.() -> Unit) =
        getBy(userId) {
            getBy(userId) {
                Api.editNickName.http()
                    .headers("token" to it?.token)
                    .params("nickName" to name)
                    .post(init)
            }
        }

    fun postHeadImg(userId: Long, file: File, init: HttpCallback<JsonResult<String>>.() -> Unit) =
        getBy(userId) {
            Api.editHeadImg.http()
                .headers(
                    "token" to it?.token,
                    "action" to UploadAction.editHeadImage
                )
                .params("file" to file)
                .post(init)
        }

}