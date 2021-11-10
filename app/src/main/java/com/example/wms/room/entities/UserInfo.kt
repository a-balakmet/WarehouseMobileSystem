package com.example.wms.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userInfo")
data class UserInfo(
    @ColumnInfo(name = "uid") val uid: Int,
    @ColumnInfo(name = "deviceID") val deviceID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "personName") val personName: String,
    @ColumnInfo(name = "personCode") val personCode: String
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

