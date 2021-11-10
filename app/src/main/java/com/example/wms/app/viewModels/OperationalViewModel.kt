package com.example.wms.app.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.wms.app.models.OperationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.wms.R
import com.example.wms.app.models.*
import com.example.wms.app.repositories.AppRepository
import com.example.wms.app.repositories.ConvertorsRepository.dateConverter
import com.example.wms.app.repositories.NetworkRepository.checkConnectionState
import com.example.wms.app.repositories.NetworkRepository.clearProductSoapForDataClass
import com.example.wms.app.repositories.NetworkRepository.createJsonForComparison
import com.example.wms.app.repositories.NetworkRepository.createJsonForOperation
import com.example.wms.network.NetworkRequest
import com.example.wms.room.entities.Cells
import com.example.wms.room.entities.Product
import com.example.wms.room.entities.UserInfo
import com.example.wms.room.repositories.CellsRepository
import com.example.wms.room.repositories.ProductsRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class OperationalViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val networkRequest: NetworkRequest,
    private val cellsRepository: CellsRepository,
    private val productsRepository: ProductsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val operationGuid = System.currentTimeMillis().toString() + "-" + UUID.randomUUID().toString()
    private val messageToInform = MutableStateFlow<String?>(null)
    val theMessage: StateFlow<String?> get() = messageToInform
    var user: UserInfo? = null

    private var operationType: Int? = null
    private var operationData: OperationData? = null

    var aCell: Cells? = null
    private var cellTo: Cells? = null
    var aPallet: MutableState<PalletData?> = mutableStateOf(null)
    var palletsList = mutableStateListOf(String())
    var fullPalletsList = mutableStateListOf<PalletData>()
    var cellsList = mutableStateListOf(String())
    var palletsInCell: MutableState<List<String>?> = mutableStateOf(null)
    private var newPallet = ""

    val isComparison = MutableStateFlow(false)
    val startedComparison: StateFlow<Boolean> get() = isComparison
    var products2compare: MutableState<List<ProductSeries>?> = mutableStateOf(null)
    var aProduct: Product? = null

    var partCode = mutableStateOf("")
    var palletsRequestedQuantity: MutableState<Int?> = mutableStateOf(null)

    var task: StorekeeperTask? = null

    var uploadingErrors = ""

    init {
        operationType = savedStateHandle["oType"]
        when (operationType) {
            -1 -> setMessage(appRepository.getStringFromInt(intValue = R.string.scan_pallet))
            1 -> setMessage(appRepository.getStringFromInt(intValue = R.string.scan_cell_from))
            0, 3, 4 -> setMessage(appRepository.getStringFromInt(intValue = R.string.scan_cell))
        }
        palletsList.removeAt(0)
        cellsList.removeAt(0)
    }

    /** basics */

    fun initOperationData() {
        operationData = OperationData(
            date = SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(Date()),
            organizationCode = "",
            stockFrom = task?.stockFromBarcode ?: "",
            stockTo = "",
            palletsQuantity = 0,
            deviceId = user!!.deviceID,
            agentCode = user!!.personCode,
            type = operationType!!,
            uniqueID = operationGuid,
            uniqueTaskId = task?.refKey ?: "",
            palletData = null
        )
    }

    fun setMessage(value: String) {
        messageToInform.value = null
        messageToInform.value = value
    }

    fun clearOnExit() {
        palletsList.clear()
        cellsList.clear()
        fullPalletsList.clear()
    }

    fun sendOperationResults() {
        CoroutineScope(IO).launch {
            if (checkConnectionState()) {
                operationData!!.palletData = fullPalletsList
                val jsonObject2send = operationType?.let { createJsonForOperation(operationData = operationData!!) }
                val body: RequestBody = jsonObject2send.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val request = networkRequest.getStringResponse(
                    token = appRepository.token,
                    suffix = "/web/req/setidentity",
                    body = body
                )
                request.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val answer = response.body()!!.byteStream().bufferedReader().use { it.readText() }
                            if (answer.contains(operationGuid)) {
                                appRepository.playSound(R.raw.complete)
                                clearOnExit()
                                setMessage(appRepository.getStringFromInt(intValue = R.string.upload_success))
                            } else {
                                uploadingErrors = answer
                                setMessage(appRepository.getStringFromInt(intValue = R.string.upload_false))
                            }
                        } else {
                            uploadingErrors = response.body()!!.byteStream().bufferedReader().use { it.readText() }
                            setMessage(appRepository.getStringFromInt(intValue = R.string.upload_false))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        uploadingErrors = "$t"
                        setMessage(appRepository.getStringFromInt(intValue = R.string.upload_false))
                    }
                })
            } else {
                uploadingErrors = appRepository.getStringFromInt(intValue = R.string.no_server_connection)
                setMessage(appRepository.getStringFromInt(intValue = R.string.no_server_connection))
            }
        }
    }

    /** working with cells */

    fun getCell(barcode: String) {
        CoroutineScope(IO).launch {
            cellsRepository.getCellByCode(code = barcode)?.let {
                if (operationType != 1 && !cellsList.contains(barcode)) {
                    cellsList.add(barcode)
                }
                if (operationType == 1) {
                    task?.let { theTask->
                        if (theTask.stockToName.contains("xxx")) {
                            cellTo = Cells(
                                cellCode = "xxx",
                                cellName = "xxx",
                                cellDescription = "xxx"
                            )
                            aCell = it
                            setMessage(appRepository.getStringFromInt(intValue = R.string.scan_pallet))
                        } else {
                            if (aCell == null) {
                                aCell = it
                                cellTo = null
                                setMessage(appRepository.getStringFromInt(intValue = R.string.scan_pallet))
                            } else {
                                cellTo = it
                                aCell = null
                                for (pallet in fullPalletsList) {
                                    if (pallet.cellTo == null) {
                                        pallet.cellTo = it
                                    }
                                }
                                if (task!!.ProductToMove.size == fullPalletsList.size) {
                                    sendOperationResults()
                                } else {
                                    setMessage(appRepository.getStringFromInt(intValue = R.string.scan_cell_from))
                                }
                            }
                        }
                    }

                } else {
                    aCell = it
                }
                appRepository.playSound(R.raw.info)
                when (operationType) {
                    0 -> setMessage(appRepository.getStringFromInt(intValue = R.string.scan_part))
                    3, 4 -> setMessage(appRepository.getStringFromInt(intValue = R.string.scan_pallet))
                }
            }
        }
    }

    fun getPalletsInCell(cellCode: String) {
        val barcodesList = ArrayList<String>()
        fullPalletsList.map { pallet ->
            if (pallet.cellTo!!.cellCode == cellCode) {
                barcodesList.add(pallet.palletBarcode)
            }
        }
        palletsInCell.value = barcodesList
    }

    fun deleteCellWithPallets(cellCode: String) {
        val list = ConcurrentLinkedQueue<PalletData>()
        fullPalletsList.map { palletData ->
            list.add(palletData)
        }
        CoroutineScope(IO).launch {
            cellsRepository.getCellByCode(code = cellCode)?.let { theCell ->
                list.forEach { palletData ->
                    if (palletData.cellTo!!.cellCode == theCell.cellCode) {
                        list.remove(palletData)
                        palletsList.remove(palletData.palletBarcode)
                    }
                }
                fullPalletsList.clear()
                list.map {
                    fullPalletsList.add(it)
                }
            }
        }
        cellsList.remove(cellCode)
    }

    private fun createACell(jsonObject: JSONObject?, fullCellList: ArrayList<String>): Cells? {
        val virtualCell: Cells?
        when (operationType) {
            1 -> {
                virtualCell = if (task!!.stockToName.contains("xxx")) cellTo else null
            }
            3, 4 -> virtualCell = if (aCell == null) {
                if (jsonObject != null) {
                    Cells(
                        cellCode = "xxx",
                        cellName = "${jsonObject.getString("m:Ячейка")} (${jsonObject.getString("m:Остаток")})",
                        cellDescription = "xxx"
                    )
                } else {
                    Cells(
                        cellCode = "xxx",
                        cellName = "${fullCellList.joinToString(separator = ",\n")})",
                        cellDescription = "xxx"
                    )
                }
            } else aCell
            else -> {
                virtualCell = if (aCell == null) {
                    if (jsonObject != null) {
                        Cells(
                            cellCode = "xxx",
                            cellName = "${jsonObject.getString("m:Ячейка")} (${jsonObject.getString("m:Остаток")})",
                            cellDescription = "xxx"
                        )
                    } else {
                        Cells(
                            cellCode = "xxx",
                            cellName = "${fullCellList.joinToString(separator = ",\n")})",
                            cellDescription = "xxx"
                        )
                    }
                } else aCell
            }
        }
        return virtualCell
    }

    /** working with pallets */

    fun getPalletData(barcode: String) {
        when (operationType) {
            1 -> {
                if (task!!.ProductToMove.size == fullPalletsList.size) {
                    appRepository.playSound(R.raw.error)
                    setMessage(appRepository.getStringFromInt(intValue = R.string.scan_cell_to))
                } else {
                    if (palletsList.contains(barcode)) {
                        setMessage("${appRepository.getStringFromInt(R.string.pallet)} $barcode ${appRepository.getStringFromInt(R.string.already_scanned)}")
                    } else {
                        requestForPallet(barcode)
                    }
                }
            }
            -1, 3, 4 -> {
                if (palletsList.contains(barcode)) {
                    appRepository.playSound(R.raw.error)
                    setMessage("${appRepository.getStringFromInt(R.string.pallet)} $barcode ${appRepository.getStringFromInt(R.string.already_scanned)}")
                } else {
                    requestForPallet(barcode)
                }
            }
        }
    }

    private fun requestForPallet(barcode: String) {
        CoroutineScope(IO).launch {
            if (checkConnectionState()) {
                val body = appRepository.createSoapBody(suffix = "GetPalletIdentity", deviceID = user!!.deviceID, barcode = barcode)
                val request = networkRequest.getStringResponse(
                    token = appRepository.token,
                    suffix = "/webservice/database.db",
                    body = body
                )
                request.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val jsonData = XML.toJSONObject(response.body()!!.byteStream().bufferedReader().use { it.readText() })
                            when (operationType) {
                                -1, 1, 3, 4 -> createPallet(jsonData, barcode)
                                0 -> {
                                    val jsonObject = jsonData.getJSONObject("soap:Envelope")
                                    val jsonResponse = jsonObject.getJSONObject("soap:Body")
                                    val jsonPromoResponse = jsonResponse.getJSONObject("m:GetPalletIdentityResponse")
                                    val jsonReturn = jsonPromoResponse.getJSONObject("m:return")
                                    if (jsonReturn.getString("m:Серия") == "XXXXXXXXXX") {
                                        if (palletsList.contains(barcode)) {
                                            appRepository.playSound(R.raw.error)
                                            setMessage("${appRepository.getStringFromInt(R.string.pallet)} $barcode ${appRepository.getStringFromInt(R.string.already_scanned)}")
                                        } else {
                                            palletsList.add(barcode)
                                            appRepository.playSound(R.raw.info)
                                            setMessage(appRepository.getStringFromInt(R.string.continue_scan))
                                        }
                                    } else {
                                        setMessage(appRepository.getStringFromInt(R.string.pallet_already_compared))
                                    }
                                }
                            }
                        } else {
                            aPallet.value = null
                            setMessage(appRepository.getStringFromInt(intValue = R.string.no_pallet_data))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        aPallet.value = null
                        setMessage(appRepository.getStringFromInt(intValue = R.string.no_pallet_data))
                    }
                })
            } else {
                aPallet.value = null
                setMessage(appRepository.getStringFromInt(intValue = R.string.no_server_connection))
            }
        }
    }

    private fun createPallet(jsonData: JSONObject, barcode: String) {
        aPallet.value = null
        CoroutineScope(IO).launch {
            val jsonObject = jsonData.getJSONObject("soap:Envelope")
            val jsonResponse = jsonObject.getJSONObject("soap:Body")
            val jsonPromoResponse = jsonResponse.getJSONObject("m:GetPalletIdentityResponse")
            val jsonReturn = jsonPromoResponse.getJSONObject("m:return")
            val jsonList1 = jsonReturn.getJSONObject("m:ЯчейкиОстаткиСписок")
            var jsonList2: JSONObject? = null
            val jsonArray2: JSONArray?
            val cellsList: ArrayList<String> = ArrayList()
            val balanceList: ArrayList<Int> = ArrayList()
            val fullCellList: ArrayList<String> = ArrayList()
            try {
                jsonList2 = jsonList1.getJSONObject("m:ЯчейкаОстаток")
            } catch (e: JSONException) {
                //e.printStackTrace()
                jsonArray2 = jsonList1.getJSONArray("m:ЯчейкаОстаток")
                for (i in 0 until jsonArray2.length()) {
                    val aCell: JSONObject = jsonArray2.getJSONObject(i)
                    cellsList.add(aCell.getString("m:Ячейка"))
                    balanceList.add(aCell.getInt("m:Остаток"))
                    fullCellList.add("${aCell.getString("m:Ячейка")} (${aCell.getInt("m:Остаток")})")
                }
            }
            var productName = ""
            productsRepository.getProductByArticle(art = jsonReturn.getString("m:Номенклатура"))?.let {
                productName = it.name
            }
            if (jsonReturn.getString("m:Серия") != "XXXXXXXXXX") {
                aPallet.value = PalletData(
                    index = palletsList.size,
                    palletBarcode = barcode,
                    productArticle = jsonReturn.getString("m:Номенклатура"),
                    productName = productName,
                    productSeries = "0${jsonReturn.getString("m:Серия")}",
                    productDate = dateConverter(oldDate = jsonReturn.getString("m:ГоденДо")),
                    productQuantity =
                    if (jsonList2 != null) jsonList2.getString("m:Остаток")
                    else balanceList.joinToString(separator = ", "),
                    cellFrom = when (operationType) {
                        1 -> aCell
                        else -> null
                    },
                    cellTo = createACell(jsonList2, fullCellList),
                    operationID = operationGuid
                )
                if (!palletsList.contains(barcode)) {
                    operationType?.let { theType ->
                        when (theType) {
                            1 -> {
                                val requestedQuantity = getRequestedQuantityOfPallets(aPallet.value!!.productArticle)
                                val scannedQuantity = getScannedQuantityOfPallets(aPallet.value!!.productArticle)
                                task?.let { theTask ->
                                    val seriesList: ArrayList<String> = ArrayList()
                                    theTask.ProductToMove.map { product ->
                                        seriesList.add(product.productArticle)
                                    }
                                    aPallet.value?.let { pallet ->
                                        /**  another checking conditions may by added here, like code of cellTo or validity date etc. */
                                        if (seriesList.contains(pallet.productArticle)) {
                                            if (requestedQuantity != scannedQuantity) {
                                                palletsList.add(barcode)
                                                fullPalletsList.add(pallet)
                                                if (theTask.ProductToMove.size == fullPalletsList.size){
                                                    if (theTask.stockToName.contains("xxx")) {
                                                        sendOperationResults()
                                                    } else {
                                                        appRepository.playSound(R.raw.error)
                                                        setMessage(appRepository.getStringFromInt(intValue = R.string.scan_cell_to))
                                                    }
                                                } else {
                                                    appRepository.playSound(R.raw.info)
                                                    setMessage(appRepository.getStringFromInt(R.string.continue_scan))
                                                }
                                            } else {
                                                appRepository.playSound(R.raw.complete)
                                                aPallet.value = null
                                                setMessage("${appRepository.getStringFromInt(R.string.article)} ${pallet.productArticle} ${appRepository.getStringFromInt(R.string.fully_scanned)}")
                                            }
                                        } else {
                                            appRepository.playSound(R.raw.error)
                                            aPallet.value = null
                                            setMessage("${appRepository.getStringFromInt(R.string.article)} ${pallet.productArticle} ${appRepository.getStringFromInt(R.string.no_in_task)}")
                                        }
                                    }
                                }
                            }
                            -1 -> {
                                palletsList.add(barcode)
                                aPallet.value?.let { fullPalletsList.add(it) }
                                appRepository.playSound(R.raw.info)
                                setMessage(appRepository.getStringFromInt(R.string.continue_scan))
                            }
                            else -> {
                                aCell?.let {
                                    aPallet.value?.let { pallet ->
                                        palletsList.add(barcode)
                                        fullPalletsList.add(pallet)
                                    }
                                    appRepository.playSound(R.raw.info)
                                    setMessage(appRepository.getStringFromInt(R.string.continue_scan))
                                }
                            }
                        }
                    }
                }
            } else {
                appRepository.playSound(R.raw.error)
                when (operationType) {
                    -1, 3 -> setMessage(value = appRepository.getStringFromInt(R.string.no_pallet_data))
                    1, 4 -> {
                        setMessage(value = "XXXXXXXXXX")
                        newPallet = barcode
                    }
                }
            }
        }
    }

    fun readScannedData(barcode: String) {
        fullPalletsList.map {
            if (it.palletBarcode == barcode) {
                aPallet.value = it
            }
        }
    }

    private fun getRequestedQuantityOfPallets(article: String): Int {
        var request = 0
        task?.let { theTask ->
            theTask.ProductToMove.let { productsList ->
                productsList.map {
                    if (it.productArticle == article) {
                        request += it.packsQuantity
                    }
                }
            }
        }
        return request
    }

    private fun getScannedQuantityOfPallets(article: String): Int {
        var scan = 0
        fullPalletsList.let { palletsList ->
            palletsList.map {
                if (it.productArticle == article) {
                    scan += 1
                }
            }
        }
        return scan
    }

    fun deletePalletData(palletData: PalletData) {
        palletsList.remove(palletData.palletBarcode)
        fullPalletsList.remove(palletData)
    }

    fun deletePalletOfComparison(palletCode: String) {
        palletsList.remove(palletCode)
    }

    /** comparison process */

    fun startComparison() {
        isComparison.value = true
        setMessage(value = appRepository.getStringFromInt(R.string.scan_goods_barcode))
    }

    fun cancelComparison() {
        isComparison.value = false
        products2compare.value = null
        setMessage(value = appRepository.getStringFromInt(R.string.comparison_stopped))
    }

    fun getProductData(barcode: String) {
        CoroutineScope(IO).launch {
            if (checkConnectionState()) {
                val body = appRepository.createSoapBody(suffix = "GetSeriesByCode", deviceID = user!!.deviceID, barcode = barcode)
                val request = networkRequest.getStringResponse(
                    token = appRepository.token,
                    suffix = "/webservice/database.db",
                    body = body
                )
                request.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val answer = response.body()!!.byteStream().bufferedReader().use { it.readText() }
                            if (answer.contains("00000000")) {
                                appRepository.playSound(R.raw.error)
                                setMessage(appRepository.getStringFromInt(intValue = R.string.no_product_received))
                            } else {
                                val productsList = clearProductSoapForDataClass(answer)
                                createProductsForComparison(productsList, barcode)
                            }
                        } else {
                            appRepository.playSound(R.raw.error)
                            setMessage(appRepository.getStringFromInt(intValue = R.string.no_product_received))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        appRepository.playSound(R.raw.error)
                        setMessage(appRepository.getStringFromInt(intValue = R.string.no_product_received))
                    }
                })
            } else {
                appRepository.playSound(R.raw.error)
                setMessage(appRepository.getStringFromInt(intValue = R.string.no_server_connection))
            }
        }
    }

    private fun createProductsForComparison(productsList: List<ProductSeries>, barcode: String) {
        CoroutineScope(IO).launch {
            aProduct = productsRepository.getProductByBarcode(code = barcode)
            products2compare.value = productsList
        }
    }

    fun comparePalletWithProduct(product: ProductSeries?) {
        CoroutineScope(IO).launch {
            if (checkConnectionState()) {
                val palletsToCompare: ArrayList<String> = ArrayList()
                var productSeries = ""
                when (operationType) {
                    0 -> {
                        palletsList.map {
                            productSeries = partCode.value
                            palletsToCompare.add(it)
                        }
                        sendComparison(productSeries, palletsToCompare)
                    }
                    /**
                     This part may be used to check comparison with product series in the task for movement
                    1 -> {
                        product?.let {
                            productSeries = if (it.seriesNo.startsWith("0")) it.seriesNo
                            else "0${it.seriesNo}"
                            task?.let { task ->
                                task.ProductToMove.let { productsList->
                                    for (aProduct in productsList) {
                                        if (aProduct.productSeries == productSeries) {
                                            palletsToCompare.add(newPallet)
                                            sendComparison(productSeries, palletsToCompare)
                                        } else {
                                            isComparison.value = false
                                            setMessage("${appRepository.getStringFromInt(R.string.series)} ${it.seriesNo} ${appRepository.getStringFromInt(R.string.no_in_task)}\n${appRepository.getStringFromInt(R.string.scan_pallet)}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    */
                    1, 4 -> {
                        product?.let {
                            productSeries = if (it.seriesNo.startsWith("0")) it.seriesNo
                            else "0${it.seriesNo}"
                        }
                        palletsToCompare.add(newPallet)
                        sendComparison(productSeries, palletsToCompare)
                    }
                }
            } else {
                appRepository.playSound(R.raw.error)
                setMessage(appRepository.getStringFromInt(intValue = R.string.no_server_connection))
            }
        }
    }

    private fun sendComparison(productSeries: String, palletsToCompare: ArrayList<String>){
        val comparisonGuid = System.currentTimeMillis().toString() + "-" + UUID.randomUUID().toString()
        val jsonObject2send = createJsonForComparison(
            comparisonGuid = comparisonGuid,
            user = user!!,
            productSeries = productSeries,
            cellCode = aCell!!.cellCode,
            palletsCodes = palletsToCompare
        )
        val body: RequestBody = jsonObject2send.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val request = networkRequest.getStringResponse(
            token = appRepository.token,
            suffix = "/web/req/setidentity",
            body = body
        )
        request.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val answer = response.body()!!.byteStream().bufferedReader().use { it.readText() }
                    if (answer.contains(comparisonGuid)) {
                        appRepository.playSound(R.raw.complete)
                        when (operationType) {
                            0 -> {
                                if (isComparison.value) {
                                    palletsRequestedQuantity.value = -1
                                } else {
                                    aCell = null
                                    partCode.value = ""
                                    setMessage(value = appRepository.getStringFromInt(intValue = R.string.scan_cell))
                                    palletsList.clear()
                                }
                            }
                            1, 4 -> {
                                appRepository.playSound(R.raw.complete)
                                isComparison.value = false
                                getPalletData(newPallet)
                                setMessage(appRepository.getStringFromInt(intValue = R.string.successful_comparison))
                            }
                        }
                    } else {
                        appRepository.playSound(R.raw.error)
                        setMessage(appRepository.getStringFromInt(intValue = R.string.no_comparison))
                    }
                } else {
                    appRepository.playSound(R.raw.error)
                    setMessage(appRepository.getStringFromInt(intValue = R.string.no_comparison))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                appRepository.playSound(R.raw.error)
                setMessage(appRepository.getStringFromInt(intValue = R.string.no_comparison))
            }
        })
    }
}