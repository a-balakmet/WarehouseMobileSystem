package com.example.wms.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.wms.room.AppDatabase
import com.example.wms.room.daos.CellsDao
import com.example.wms.room.daos.PersonnelDao
import com.example.wms.room.daos.ProductDao
import com.example.wms.room.daos.UserInfoDao
import com.example.wms.room.repositories.CellsRepository
import com.example.wms.room.repositories.PersonnelRepository
import com.example.wms.room.repositories.ProductsRepository
import com.example.wms.room.repositories.UserInfoRepository

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

   @Provides
   fun provideUserInfoDao(@ApplicationContext app: Context): UserInfoDao {
       return AppDatabase.getInstance(app).userInfoDao
   }

    @Provides
    fun provideUserInfoRepository(userInfoDao: UserInfoDao) = UserInfoRepository(userInfoDao)

    @Provides
    fun provideCellsDao(@ApplicationContext app: Context): CellsDao {
        return AppDatabase.getInstance(app).cellsDao
    }

    @Provides
    fun provideCellsRepository(cellsDao: CellsDao) = CellsRepository(cellsDao)

    @Provides
    fun providePersonnelDao(@ApplicationContext app: Context): PersonnelDao {
        return AppDatabase.getInstance(app).personnelDao
    }

    @Provides
    fun providePersonnelRepository(personnelDao: PersonnelDao) = PersonnelRepository(personnelDao)

    @Provides
    fun provideProductsDao(@ApplicationContext app: Context): ProductDao {
        return AppDatabase.getInstance(app).productDao
    }

    @Provides
    fun provideProductsRepository(productDao: ProductDao) = ProductsRepository(productDao)
}