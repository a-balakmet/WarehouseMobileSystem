package com.example.wms.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wms.room.entities.Cells

@Dao
interface CellsDao {

    @Insert
    suspend fun insert(cell: Cells)

    @Query("DELETE FROM cells")
    suspend fun clearCells()

    @Query("SELECT * FROM cells WHERE cellCode =:code LIMIT 1")
    fun findCellByCode(code: String) : Cells?
}