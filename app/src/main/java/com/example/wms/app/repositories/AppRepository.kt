package com.example.wms.app.repositories

import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import com.example.wms.ThisApp
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import kotlin.system.exitProcess

class AppRepository @Inject constructor(private val application: ThisApp) {

    val token by lazy { Credentials.basic("login", "password")}

    fun openDateSettings() {
        val openDate = Intent(Settings.ACTION_DATE_SETTINGS)
        openDate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(openDate)
        exitProcess(0)
    }

    fun restartApp() {
        val packageManager: PackageManager = application.packageManager
        val intent = packageManager.getLaunchIntentForPackage(application.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        application.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    fun closeApp() {
        exitProcess(0)
    }

    fun openNetworkSettings(isStart: Boolean) {
        val openWifi = Intent(Settings.ACTION_WIFI_SETTINGS)
        openWifi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(openWifi)
        if (isStart) {
            exitProcess(0)
        }
    }

    fun openBluetoothSettings() {
        val openBT = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        openBT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(openBT)
    }

    fun getStringFromInt(intValue: Int) = application.getString(intValue)

    fun createSoapBody(suffix: String, deviceID: String, barcode: String) : RequestBody {
        val barcodeType = when(suffix) {
            "GetPallet" -> "PalletNo"
            "GetSeries" -> "SKUBarcode"
            else -> ""
        }
        val soapRequest =
            "<v:Envelope xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:d=\"http://www.w3.org/2001/XMLSchema\" xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "<v:Header />\n" +
                    "<v:Body>\n" +
                    "<$suffix xmlns=\"http://www.com.example\" id=\"o0\" c:root=\"1\">\n" +
                    "<n0:DeviceID xmlns:n0=\"http://www.example.com\">${deviceID}</n0:DeviceID>\n" +
                    "<n1:$barcodeType xmlns:n1=\"http://www.example.com\">$barcode</n1:$barcodeType>\n" +
                    "</$suffix>\n" +
                    "</v:Body>\n" +
                    "</v:Envelope>"
        return soapRequest.toRequestBody("text/xml".toMediaTypeOrNull())
    }

    fun playSound(sound: Int) {
        try {
            val uri = "android.resource://com.example.wms/$sound"
            val notification = Uri.parse(uri)
            val r = RingtoneManager.getRingtone(application, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}