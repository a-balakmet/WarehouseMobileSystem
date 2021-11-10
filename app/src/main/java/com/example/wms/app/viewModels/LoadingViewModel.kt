package com.example.wms.app.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import com.example.wms.R
import com.example.wms.app.repositories.AppRepository
import com.example.wms.app.sharedPrefs.PreferenceRepository
import com.example.wms.network.NetworkRequest
import com.example.wms.room.entities.Cells
import com.example.wms.room.entities.Personnel
import com.example.wms.room.entities.Product
import com.example.wms.room.entities.UserInfo
import com.example.wms.room.repositories.CellsRepository
import com.example.wms.room.repositories.PersonnelRepository
import com.example.wms.room.repositories.ProductsRepository
import com.example.wms.room.repositories.UserInfoRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import org.json.XML
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val prefs: PreferenceRepository,
    private val networkRequest: NetworkRequest,
    private val personnelRepository: PersonnelRepository,
    private val cellsRepository: CellsRepository,
    private val productsRepository: ProductsRepository,
    private val userInfoRepository: UserInfoRepository
) : ViewModel() {

    var personnelLoadingState: MutableState<Int?> = mutableStateOf(null)
    var cellsLoadingState: MutableState<Int?> = mutableStateOf(null)
    var productsLoadingState: MutableState<Int?> = mutableStateOf(null)
    var progressFulfillment: MutableState<ArrayList<Boolean>> = mutableStateOf(arrayListOf(false, false, false))
    var loadingErrors: MutableState<String?> = mutableStateOf(null)

    lateinit var userInfo: UserInfo

    init {
        executeLoading()
        CoroutineScope(IO).launch { userInfo = userInfoRepository.getUser() }
    }

    /** run loading */

    fun executeLoading() {
        val guid = prefs.getStringValue("guid")!!
        val loadingTasks = arrayListOf("/web/req/getworkerslist", "/webservice/database.db", "/web/req/getproducts")
        personnelLoadingState.value = R.string.load_personnel
        cellsLoadingState.value = R.string.load_cells
        productsLoadingState.value = R.string.load_products
        loadingErrors.value = ""
        for (aTask in loadingTasks) {
            CoroutineScope(IO).launch {
                val soapRequest =
                    "<v:Envelope xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:d=\"http://www.w3.org/2001/XMLSchema\" xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                            "<v:Header />" +
                            "<v:Body>" +
                            "<GetCellDescription xmlns=\"http://www.com.example\" id=\"o0\" c:root=\"1\">" +
                            "<n0:DeviceID xmlns:n0=\"http://www.com.example\">${guid}</n0:DeviceID>" +
                            "</GetCellDescription>" +
                            "</v:Body>" +
                            "</v:Envelope>"
                val body: RequestBody = if (aTask.contains("hs"))
                    "DeviceID=${guid}".toRequestBody("text/plain".toMediaTypeOrNull())
                else soapRequest.toRequestBody("text/xml".toMediaTypeOrNull())
                val request = networkRequest.getStringResponse(
                    token = appRepository.token,
                    suffix = aTask,
                    body = body
                )
                request.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val answer = response.body()!!.byteStream().bufferedReader().use { it.readText() }
                            when {
                                aTask.contains("getworkerslist") -> savePersonnel(answer)
                                aTask.contains("getproducts") -> saveProducts(answer)
                                aTask.contains("webservice") -> saveCells(answer)
                            }
                        } else {
                            when {
                                aTask.contains("getworkerslist") -> personnelLoadingState.value = R.string.personnel_non_loaded
                                aTask.contains("getproducts") -> productsLoadingState.value = R.string.products_non_loaded
                                aTask.contains("webservice") -> productsLoadingState.value = R.string.cells_non_loaded
                            }
                            playSound(R.raw.error)
                            val errors = response.body()!!.byteStream().bufferedReader().use { it.readText() }
                            loadingErrors.value = "$aTask: ${loadingErrors.value}\n$errors"
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        when {
                            aTask.contains("getworkerslist") -> personnelLoadingState.value = R.string.personnel_non_loaded
                            aTask.contains("getproducts") -> productsLoadingState.value = R.string.products_non_loaded
                            aTask.contains("webservice") -> productsLoadingState.value = R.string.cells_non_loaded
                        }
                        playSound(R.raw.error)
                        loadingErrors.value = "$aTask: ${loadingErrors.value}\n$t"
                    }
                })
            }
        }
    }

    /** save received data */

    private fun savePersonnel(content: String) {
        CoroutineScope(IO).launch {
            val jsonObject = JSONObject(content)
            if (jsonObject.has("value")) {
                personnelRepository.clearPersonnel()
                val personsList: ArrayList<Personnel> = ArrayList()
                val jsonArray = jsonObject.getJSONArray("value")
                for (i in 0 until jsonArray.length()) {
                    val jsonPerson = jsonArray.getJSONObject(i)
                    var personBarcode = ""
                    val additions = jsonPerson.getJSONArray("ДополнительныеРеквизиты")
                    for (j in 0 until additions.length()) {
                        val addObject = additions.getJSONObject(j)
                        val tempKey = addObject.getString("Свойство_Key")
                        if (tempKey == "12345678-aaaa-bbbb-cccc-123456789012") {
                            personBarcode = addObject.getString("Значение")
                        }
                    }
                    val person = Personnel(
                        name = jsonPerson.getString("Description"),
                        code = jsonPerson.getString("Ref_Key"),
                        barcode = personBarcode
                    )
                    personsList.add(person)
                }
                personnelRepository.savePersonnel(persons = personsList)
                personnelLoadingState.value = R.string.personnel_loaded
                progressFulfillment.value[0] = true
            } else {
                personnelLoadingState.value = R.string.personnel_non_loaded
                loadingErrors.value = "${loadingErrors.value}\n$content"
            }
        }
    }

    private fun saveProducts(content: String) {
        CoroutineScope(IO).launch {
            val jsonResponse = XML.toJSONObject(content)
            if (jsonResponse.has("НоменклатураСписок")) {
                productsRepository.clearProducts()
                val productsList: ArrayList<Product> = ArrayList()
                val allProducts = jsonResponse.getJSONObject("НоменклатураСписок")
                val productsArray = allProducts.getJSONArray("НоменклатураПозиция")
                for (i in 0 until productsArray.length()) {
                    val jsonObject = productsArray.getJSONObject(i)
                    val product = Product(
                        article = jsonObject.getString("Артикул"),
                        name = jsonObject.getString("Номенклатура"),
                        barcode = jsonObject.getString("Страна")
                    )
                    productsList.add(product)
                }
                productsRepository.saveProducts(products = productsList)
                productsLoadingState.value = R.string.products_loaded
                progressFulfillment.value[1] = true
            } else {
                productsLoadingState.value = R.string.personnel_non_loaded
                loadingErrors.value = "${loadingErrors.value}\n$content"
            }
        }
    }

    private fun saveCells(content: String) {
        CoroutineScope(IO).launch {
            val jsonData = XML.toJSONObject(content)
            val jsonObject = jsonData.getJSONObject("soap:Envelope")
            val jsonResponse = jsonObject.getJSONObject("soap:Body")
            val jsonPromoResponse = jsonResponse.getJSONObject("m:GetCellDescriptionResponse")
            val jsonReturn = jsonPromoResponse.getJSONObject("m:return")
            if (jsonReturn.has("m:ЯчейкаОписание")) {
                cellsRepository.clearCells()
                val cellsList: ArrayList<Cells> = ArrayList()
                val jsonArray = jsonReturn.getJSONArray("m:ЯчейкаОписание")
                for (i in 0 until jsonArray.length()) {
                    val jsonCell = jsonArray.getJSONObject(i)
                    val cell = Cells(
                        cellCode = "0${jsonCell.getString("m:ШтрихКод")}",
                        cellName = jsonCell.getString("m:Наименование"),
                        cellDescription = jsonCell.getString("m:Описание")
                    )
                    cellsList.add(cell)
                }
                cellsRepository.saveCells(cells = cellsList)
                cellsLoadingState.value = R.string.cells_loaded
                progressFulfillment.value[2] = true
            } else {
                cellsLoadingState.value = R.string.cells_non_loaded
                loadingErrors.value = "${loadingErrors.value}\n$content"
            }
        }
    }

    fun playSound(sound: Int) {
        appRepository.playSound(sound)
    }
}