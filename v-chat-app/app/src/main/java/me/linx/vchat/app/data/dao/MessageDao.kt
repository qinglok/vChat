package me.linx.vchat.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.linx.vchat.app.data.entity.Message

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(msg: Message): Long

    @Query("SELECT * FROM message where (fromId = :fromId AND toId = :toId) OR (toId = :fromId AND fromId = :toId) ORDER BY updateTime ")
    fun findByFromAndTo(fromId: Long, toId: Long): List<Message>

    @Query("SELECT * FROM message WHERE sent = :sent AND fromId = :userId")
    fun findBySent(userId: Long, sent: Boolean): List<Message>

    @Query("SELECT COUNT(*) FROM message WHERE toId =:userId AND read = :read")
    fun countByUserAndRead(userId: Long, read: Boolean): Int

    @Query("UPDATE message set read = :read WHERE toId =:userId")
    fun updateReadStatus(userId: Long, read: Boolean)
}