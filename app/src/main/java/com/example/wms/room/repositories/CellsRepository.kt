package com.example.wms.room.repositories

import com.example.wms.room.daos.CellsDao
import com.example.wms.room.entities.Cells
import javax.inject.Inject

class CellsRepository @Inject constructor(private val cellsDao: CellsDao) {

    suspend fun saveCells(cells: ArrayList<Cells>) {
        cells.forEach { cell ->
            cellsDao.insert(cell = cell)
        }
    }

    suspend fun clearCells() = cellsDao.clearCells()

    fun getCellByCode(code: String) = cellsDao.findCellByCode(code = code)
}