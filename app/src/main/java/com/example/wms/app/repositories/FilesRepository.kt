package com.example.wms.app.repositories

import java.io.*
import java.lang.Exception

object FilesRepository {

    fun readSavedGUID(): String {
        val myExternalFile = File("sdcard/", "guid.txt")
        return if (myExternalFile.exists()) {
            val inputStream: FileInputStream?
            return try {
                inputStream = FileInputStream(myExternalFile)
                val sb = StringBuffer()
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line = reader.readLine()
                while (line != null) {
                    sb.append(line)
                    line = reader.readLine()
                }
                sb.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        } else ""
    }

    fun saveDeviceGUID(GUID: String) {
        val file = File("sdcard/", "guid.txt")
        val outputStream: FileOutputStream
        try {
            if (file.createNewFile()) {
                file.createNewFile()
                outputStream = FileOutputStream(file, true)
                outputStream.write(GUID.toByteArray())
                outputStream.flush()
                outputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteSavedGuid() {
        try {
            File("sdcard/", "guid.txt").let {
                if (it.exists()) {
                    it.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}