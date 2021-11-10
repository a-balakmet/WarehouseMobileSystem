package com.example.wms.app.repositories

import java.text.SimpleDateFormat
import java.util.*

object ConvertorsRepository {

    fun dateConverter(oldDate: String): String {
        val incomingDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val returnedDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return returnedDateFormat.format(incomingDateFormat.parse(oldDate)!!)
    }

    fun dateConverterToUpload(oldDate: String): String {
        val incomingDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val returnedDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return returnedDateFormat.format(incomingDateFormat.parse(oldDate)!!)
    }
}