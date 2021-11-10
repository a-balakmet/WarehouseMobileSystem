package com.example.wms.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wms.room.daos.CellsDao
import com.example.wms.room.daos.PersonnelDao
import com.example.wms.room.daos.ProductDao
import com.example.wms.room.daos.UserInfoDao
import com.example.wms.room.entities.Cells
import com.example.wms.room.entities.Personnel
import com.example.wms.room.entities.Product
import com.example.wms.room.entities.UserInfo

@Database(entities = [UserInfo::class, Cells::class, Personnel::class, Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val userInfoDao: UserInfoDao
    abstract val cellsDao: CellsDao
    abstract val personnelDao: PersonnelDao
    abstract val productDao: ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "wms.db")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}