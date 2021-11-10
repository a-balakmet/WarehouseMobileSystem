package com.example.wms.app.repositories

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.wms.app.models.OperationData
import com.example.wms.app.models.ProductSeries
import com.example.wms.app.models.ProductToCompare
import com.example.wms.app.repositories.ConvertorsRepository.dateConverterToUpload
import com.example.wms.room.entities.UserInfo
import org.json.JSONArray
import org.json.JSONObject
import org.json.XML
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object NetworkRepository {

    private val fullDateFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH) }

    suspend fun checkConnectionState(): Boolean = withContext(Dispatchers.IO) {
        isOnline()
    }

    private fun isOnline(): Boolean {
        return try {
            val socket = Socket()
            val socketAddress: SocketAddress = InetSocketAddress("example.com", 80)
            socket.connect(socketAddress, 3000)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    fun createJsonForOperation(operationData: OperationData): JSONObject {
        val jsonObject2send = JSONObject()
        jsonObject2send.put("DeviceID", operationData.deviceId)
        val jsonArray2send = JSONArray()
        val identityObject = JSONObject()
        identityObject.put("Дата", fullDateFormat.format(Date()))
        identityObject.put("ОрганизацияКод", "")
        identityObject.put("СкладОткуда", "")
        identityObject.put("СкладКуда", "")
        identityObject.put("КоличествоПаллет", "0")
        identityObject.put("КодАгента", operationData.agentCode)
        identityObject.put("Тип", operationData.type.toString())
        identityObject.put("УникальныйИдентификатор", operationData.uniqueID)
        identityObject.put("УникальныйИдентификаторЗадания", operationData.uniqueTaskId)
        val palletArray = JSONArray()
        for (pallet in operationData.palletData!!) {
            val onePallet = JSONObject()
            onePallet.put(
                "ЯчейкаОткуда", when (operationData.type) {
                    1 -> pallet.cellFrom!!.cellCode
                    else -> ""
                }
            )
            onePallet.put("ЯчейкаКуда", pallet.cellTo!!.cellCode)
            onePallet.put(
                "Серия", when (operationData.type) {
                    1, 4 -> pallet.productSeries
                    else -> ""
                }
            )
            onePallet.put("Номенклатура", "")
            onePallet.put(
                "ДатаСерии", when (operationData.type) {
                    1, 4 -> dateConverterToUpload(pallet.productDate)
                    else -> ""
                }
            )
            onePallet.put("НомерПаллеты", pallet.palletBarcode)
            onePallet.put("Количество", "1")
            onePallet.put("UID", operationData.uniqueID)
            palletArray.put(onePallet)
        }
        identityObject.put("Товары", palletArray)
        jsonArray2send.put(identityObject)
        jsonObject2send.put("ИдентификацияПартии", jsonArray2send)
        return jsonObject2send
    }

    fun createJsonForComparison(comparisonGuid: String, user: UserInfo, productSeries: String, cellCode: String, palletsCodes: ArrayList<String>): JSONObject {
        val jsonObject2send = JSONObject()
        jsonObject2send.put("DeviceID", user.deviceID)
        val jsonArray2send = JSONArray()
        val identityObject = JSONObject()
        identityObject.put("Дата", fullDateFormat.format(Date()))
        identityObject.put("ОрганизацияКод", "")
        identityObject.put("СкладОткуда", "")
        identityObject.put("СкладКуда", "")
        identityObject.put("КоличествоПаллет", "0")
        identityObject.put("КодАгента", user.personCode)
        identityObject.put("Тип", "0")
        identityObject.put("УникальныйИдентификатор", comparisonGuid)
        identityObject.put("УникальныйИдентификаторЗадания", "")
        val palletArray = JSONArray()
        palletsCodes.map { palletCode ->
            val onePallet = JSONObject()
            onePallet.put("ЯчейкаОткуда", "")
            onePallet.put("ЯчейкаКуда", cellCode)
            onePallet.put("Серия", productSeries)
            onePallet.put("Номенклатура", "")
            onePallet.put("ДатаСерии", "")
            onePallet.put("НомерПаллеты", palletCode)
            onePallet.put("Количество", "1")
            onePallet.put("UID", comparisonGuid)
            palletArray.put(onePallet)
        }
        identityObject.put("Товары", palletArray)
        jsonArray2send.put(identityObject)
        jsonObject2send.put("ИдентификацияПартии", jsonArray2send)
        return jsonObject2send
    }

    fun clearProductSoapForDataClass(input: String): List<ProductSeries> {
        val clearedAnswer = input
            .replace("soap:", "soap")
            .replace("xmlns:", "xmlns")
            .replace("m:", "")
            .replace("soapEnvelope", "SoapEnvelopeProduct")
            .replace("soapBody", "SoapBodyProduct")
            .replace("return", "TheReturnProduct")
            .replace("xsi:", "")
            .replace("СписокСерий", "SeriesList")
            .replace("СерияТовара", "ProductSeries")
            .replace("НомерСерии", "seriesNo")
            .replace("ДатаПроизводства", "productionDate")
        val jsonData = XML.toJSONObject(clearedAnswer)
        val productsList = Gson().fromJson(jsonData.toString(), ProductToCompare::class.java)
        return productsList.SoapEnvelopeProduct.SoapBodyProduct.GetSeriesByCodeResponse.TheReturnProduct.ProductSeries
    }
}