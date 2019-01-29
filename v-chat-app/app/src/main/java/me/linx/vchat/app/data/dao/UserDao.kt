package me.linx.vchat.app.data.dao

import androidx.room.*
import me.linx.vchat.app.data.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE bizId = :userId")
    suspend fun findByBizId(userId: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)
}