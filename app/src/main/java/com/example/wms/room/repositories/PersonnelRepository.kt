package com.example.wms.room.repositories

import com.example.wms.room.daos.PersonnelDao
import com.example.wms.room.entities.Personnel
import javax.inject.Inject

class PersonnelRepository @Inject constructor(private val personnelDao: PersonnelDao) {

    suspend fun savePersonnel(persons: ArrayList<Personnel>) {
        for (person in persons) {
            personnelDao.insert(person = person)
        }
    }

    suspend fun clearPersonnel(){
        personnelDao.clearPersonnel()
    }

    fun getPerson(barcode: String) = personnelDao.findPersonByBarcode(code = barcode)
}