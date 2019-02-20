package me.linx.vchat.app.data.im.session

import io.netty.util.AttributeKey
import me.linx.vchat.app.data.entity.User

import java.security.PrivateKey


object Attributes {
    val private_key: AttributeKey<PrivateKey> by lazy {
        AttributeKey.newInstance<PrivateKey>(PrivateKey::class.java.name)
    }

    val user: AttributeKey<User> by lazy {
        AttributeKey.newInstance<User>(User::class.java.name)
    }

}
