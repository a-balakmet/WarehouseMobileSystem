package com.example.wms.room.repositories

import com.example.wms.room.daos.UserInfoDao
import com.example.wms.room.entities.UserInfo
import javax.inject.Inject

class UserInfoRepository @Inject constructor(private val userInfoDao: UserInfoDao) {

    suspend fun saveUser(userInfo: UserInfo) {
        userInfoDao.insert(userInfo = userInfo)
    }

    suspend fun clearUserTable() {
        userInfoDao.clearUserInfo()
    }

    fun getUser(): UserInfo = userInfoDao.findUser()
}