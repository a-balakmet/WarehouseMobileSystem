package com.example.wms.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cells")
data class Cells(
    @ColumnInfo(name = "cellCode") val cellCode: String,
    @ColumnInfo(name = "cellName") val cellName: String,
    @ColumnInfo(name = "cellDescription") val cellDescription: String
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}




