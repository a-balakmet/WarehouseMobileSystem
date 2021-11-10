package com.example.wms.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wms.room.entities.Personnel

@Dao
interface PersonnelDao {

    @Insert
    suspend fun insert(person: Personnel)

    @Query("DELETE FROM personnel")
    suspend fun clearPersonnel()

    @Query("SELECT * FROM personnel WHERE barcode =:code")
    fun findPersonByBarcode(code: String) : Personnel?
}