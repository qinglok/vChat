package me.linx.vchat.app.data.repository

import com.blankj.utilcode.util.DeviceUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.api.UploadAction
import me.linx.vchat.app.data.db.AppDatabase
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.net.HttpCallback
import me.linx.vchat.app.net.JsonResult
import me.linx.vchat.app.net.http
import me.linx.vchat.app.net.post
import java.io.File

class UserRepository {
    private val userDao by lazy { AppDatabase.db.userDao() }

    fun save(user: User) = GlobalScope.launch {
        userDao.insert(user)
    }

    fun getByAsync(userId: Long) = GlobalScope.async {
        userDao.findByBizId(userId)
    }

    fun sign(api: String, email: String?, password: String?, init: HttpCallback<JsonResult<User>>.() -> Unit) =
        api.http()
            .params(
                "email" to email,
                "password" to password,
                "deviceId" to DeviceUtils.getAndroidID()
            )
            .post(init)

    fun postHeadImg(userId: Long, file: File, init: HttpCallback<JsonResult<String>>.() -> Unit) =
        GlobalScope.launch {
            getByAsync(userId).await()?.let { user ->
                Api.editHeadImg.http()
                    .headers(
                        "token" to user.token,
                        "action" to UploadAction.editHeadImage
                    )
                    .params("file" to file)
                    .post(init)
            }
        }

}