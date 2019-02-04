package me.linx.vchat.app.data.repository

import com.blankj.utilcode.util.DeviceUtils
import kotlinx.coroutines.*
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.api.UploadAction
import me.linx.vchat.app.data.db.AppDatabase
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.net.HttpCallback
import me.linx.vchat.app.net.JsonResult
import me.linx.vchat.app.net.http
import me.linx.vchat.app.net.post
import me.linx.vchat.app.utils.launch
import java.io.File
import java.util.*

class UserRepository private constructor() {
    private val userDao by lazy { AppDatabase.db.userDao() }

    companion object {
        val instance by lazy { UserRepository() }
    }

    fun saveAsync(user: User) =
        GlobalScope.async {
            userDao.insert(user)
        }

    fun getByAsync(userId: Long) =
        GlobalScope.async {
            userDao.findByBizId(userId)
        }

    fun login(email: String?, password: String?, init: HttpCallback<JsonResult<User>>.() -> Unit) =
        Api.login.http()
            .params(
                "email" to email,
                "password" to password,
                "deviceId" to DeviceUtils.getAndroidID() + UUID.randomUUID().toString() + System.currentTimeMillis().toString()
            )
            .post(init)

    fun register(
        email: String?,
        password: String?,
        secretQuestion: String?,
        secretAnswer: String?,
        init: HttpCallback<JsonResult<User>>.() -> Unit
    ) =
        Api.register.http()
            .params(
                "email" to email,
                "password" to password,
                "secretQuestion" to secretQuestion,
                "secretAnswer" to secretAnswer,
                "deviceId" to DeviceUtils.getAndroidID() + UUID.randomUUID().toString() + System.currentTimeMillis().toString()
            )
            .post(init)

    fun postNickName(userId: Long, name: String?, init: HttpCallback<JsonResult<String>>.() -> Unit) =
        getByAsync(userId).launch {
            Api.editNickName.http()
                .headers("token" to it?.token)
                .params("nickName" to name)
                .post(init)
        }

    fun postHeadImg(userId: Long, file: File, init: HttpCallback<JsonResult<String>>.() -> Unit) =
        getByAsync(userId).launch {
            Api.editHeadImg.http()
                .headers(
                    "token" to it?.token,
                    "action" to UploadAction.editHeadImage
                )
                .params("file" to file)
                .post(init)
        }


    fun logout(userId: Long, init: HttpCallback<JsonResult<Unit>>.() -> Unit) {
        getByAsync(userId).launch {
            Api.logout.http()
                .headers(
                    "token" to it?.token
                )
                .post(init)
        }
    }

    fun loginAndVerifySecret(
        email: String?,
        password: String?,
        answer: String?,
        init: HttpCallback<JsonResult<User>>.() -> Unit
    ) =
        Api.loginAndVerifySecret.http()
            .params(
                "email" to email,
                "password" to password,
                "answer" to answer,
                "deviceId" to DeviceUtils.getAndroidID() + UUID.randomUUID().toString() + System.currentTimeMillis().toString()
            )
            .post(init)

}