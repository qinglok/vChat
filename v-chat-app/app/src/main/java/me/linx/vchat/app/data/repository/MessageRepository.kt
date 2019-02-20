package me.linx.vchat.app.data.repository

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.linx.vchat.app.data.db.AppDatabase
import me.linx.vchat.app.data.entity.Message

class MessageRepository private constructor() {
    private val dao by lazy { AppDatabase.db.messageDao() }

    companion object {
        val instance by lazy { MessageRepository() }
    }

    fun saveAsync(msg: Message?) =
        GlobalScope.async {
            msg?.let {
                dao.insert(it)
            }
        }

    fun getByFromAndToAsync(fromId: Long, toId: Long) =
        GlobalScope.async {
            dao.findByFromAndTo(fromId, toId)
        }

    fun getUnSendAsync(userId: Long) =
        GlobalScope.async {
            dao.findBySent(userId, false)
        }

    fun queryUnReadAsync(userId: Long) =
        GlobalScope.async {
            dao.countByUserAndRead(userId, false)
        }

    fun clearUnReadAsync(userId: Long) =
        GlobalScope.async {
            dao.updateReadStatus(userId, true)
        }
}