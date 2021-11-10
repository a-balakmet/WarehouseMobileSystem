package com.example.wms.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wms.room.entities.UserInfo

@Dao
interface UserInfoDao {

    @Query("SELECT * FROM userInfo WHERE uid = 1")
    fun findUser(): UserInfo

    @Insert
    suspend fun insert(userInfo: UserInfo)

    @Query("DELETE FROM userInfo")
    suspend fun clearUserInfo()
}