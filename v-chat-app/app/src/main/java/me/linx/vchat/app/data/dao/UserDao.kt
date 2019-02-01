package me.linx.vchat.app.data.dao

import androidx.room.*
import me.linx.vchat.app.data.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE bizId = :userId")
    fun findByBizId(userId: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User) : Long?

    @Delete
    fun delete(user: User)
}