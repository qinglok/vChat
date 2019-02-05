package me.linx.vchat.app.data.repository

import com.blankj.utilcode.util.DeviceUtils
import kotlinx.coroutines.*
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.api.UploadAction
import me.linx.vchat.app.data.db.AppDatabase
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.net.*
import java.io.File
import java.util.*

class UserRepository private constructor() {
    private val userDao by lazy { AppDatabase.db.userDao() }

    companion object {
        val instance by lazy { UserRepository() }
    }

    fun saveAsync(user: User?) =
        GlobalScope.async {
            user?.let {
                userDao.insert(it)
            }
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

    fun postNickName(user :User, name: String?, init: HttpCallback<JsonResult<String>>.() -> Unit) =
            Api.editNickName.http()
                .headers("token" to user.token)
                .params("nickName" to name)
                .post(init)

    fun postHeadImg(user :User, file: File, init: HttpCallback<JsonResult<String>>.() -> Unit) =
            Api.editHeadImg.http()
                .headers(
                    "token" to user.token,
                    "action" to UploadAction.editHeadImage
                )
                .params("file" to file)
                .post(init)

    fun logout(user :User, init: HttpCallback<JsonResult<Unit>>.() -> Unit) {
            Api.logout.http()
                .headers(
                    "token" to user.token
                )
                .post(init)
    }

    fun getUserProfile(user :User?, init: HttpCallback<JsonResult<User>>.() -> Unit) {
        Api.getUserProfile.http()
            .headers(
                "token" to user?.token
            )
            .params("updateTime" to user?.updateTime)
            .get(init)
    }

}