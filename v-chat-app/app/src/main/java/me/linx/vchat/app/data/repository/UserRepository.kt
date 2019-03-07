package me.linx.vchat.app.data.repository

import android.graphics.Bitmap
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.linx.vchat.app.data.api.Api
import me.linx.vchat.app.data.api.UploadAction
import me.linx.vchat.app.data.db.AppDatabase
import me.linx.vchat.app.data.entity.User
import me.linx.vchat.app.net.*
import me.linx.vchat.app.utils.GlideApp
import me.linx.vchat.app.widget.GlideRoundTransform
import java.io.File
import java.util.*

class UserRepository private constructor() {
    private val dao by lazy { AppDatabase.db.userDao() }

    companion object {
        val instance by lazy { UserRepository() }
    }

    fun saveAsync(user: User?) =
        GlobalScope.async {
            user?.also {
                dao.insert(user)
            }
        }

    fun saveAsync(users: List<User>?) =
        GlobalScope.async {
            users?.also {
                dao.insert(users)
            }
        }

    fun loadAvatarBitmapAsync(avatar: String?): Deferred<Bitmap> =
        GlobalScope.async {
            GlideApp.with(Utils.getApp())
                .asBitmap()
                .load(Api.baseFileDir + avatar)
                .transform(GlideRoundTransform(4))
                .submit()
                .get()
        }

    fun getByAsync(userId: Long) =
        GlobalScope.async {
            dao.findByBizId(userId)
        }

    fun login(email: String?, password: String?, init: HttpHandler<JsonResult<User>>.() -> Unit) =
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
        init: HttpHandler<JsonResult<User>>.() -> Unit
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
        init: HttpHandler<JsonResult<User>>.() -> Unit
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

    fun postNickname(user: User, name: String?, init: HttpHandler<JsonResult<String>>.() -> Unit) =
        Api.editNickname.http()
            .headers("token" to user.token)
            .params("nickname" to name)
            .post(init)

    fun postAvatar(user: User, file: File, init: HttpHandler<JsonResult<String>>.() -> Unit) =
        Api.editAvatar.http()
            .headers(
                "token" to user.token,
                "action" to UploadAction.editHeadImage
            )
            .params("file" to file)
            .post(init)

    fun logout(user: User, init: HttpHandler<JsonResult<Unit>>.() -> Unit) {
        Api.logout.http()
            .headers(
                "token" to user.token
            )
            .post(init)
    }

    fun getUserProfile(token: String, lastUpdateTime: Long?, init: HttpHandler<JsonResult<User>>.() -> Unit) {
        Api.getUserProfile.http()
            .headers(
                "token" to token
            )
            .params("updateTime" to lastUpdateTime)
            .get(init)
    }

    fun getUserProfile(
        token: String,
        targetUserId: Long,
        lastUpdateTime: Long?,
        init: HttpHandler<JsonResult<User>>.() -> Unit
    ) {
        Api.getUserProfileById.http()
            .headers(
                "token" to token
            )
            .params(
                "userId" to targetUserId,
                "updateTime" to lastUpdateTime
            )
            .get(init)
    }

}