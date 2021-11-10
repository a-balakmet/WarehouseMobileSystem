package com.example.wms.app.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import com.example.wms.R
import com.example.wms.app.models.ServerDate
import com.example.wms.app.repositories.AppRepository
import com.example.wms.app.repositories.FilesRepository
import com.example.wms.app.repositories.NetworkRepository.checkConnectionState
import com.example.wms.app.sharedPrefs.PreferenceRepository
import com.example.wms.network.NetworkRequest
import com.example.wms.room.entities.UserInfo
import com.example.wms.room.repositories.UserInfoRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.XML
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val prefs: PreferenceRepository,
    private val networkRequest: NetworkRequest,
    private val userInfoRepository: UserInfoRepository
): ViewModel() {

    var authState = MutableLiveData<Int>()
    var guid: String = ""

    init {
        checkDeviceDate()
    }

    private fun initGuid() {
        guid = when {
            FilesRepository.readSavedGUID() != "" -> FilesRepository.readSavedGUID()
            prefs.getStringValue("guid") != "" -> prefs.getStringValue("guid")!!
            else -> UUID.randomUUID().toString()
        }
    }

    private fun checkDeviceDate() {
        authState.value = R.string.check_date
        CoroutineScope(Dispatchers.IO).launch {
            initGuid()
            prefs.setValue("guid", guid)
            if (checkConnectionState()) {
                val body: RequestBody = "DeviceID=${guid}".toRequestBody("text/plain".toMediaTypeOrNull())
                val request = networkRequest.getDateResponse(
                    token = appRepository.token,
                    suffix = "web/req/getcheckconnection",
                    body = body
                )
                request.enqueue(object : Callback<ServerDate> {
                    override fun onResponse(call: Call<ServerDate>, response: Response<ServerDate>) {
                        val serverDate = response.body()!!
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
                        if (serverDate.Data == dateFormat.format(Date())) {
                            getUserData()
                        } else {
                            authState.value = R.string.wrong_date
                        }
                    }

                    override fun onFailure(call: Call<ServerDate>, t: Throwable) {
                        authState.value = R.string.no_net
                    }
                })
            } else {
                authState.value = R.string.no_net
            }
        }
    }

    private fun getUserData() {
        authState.value = R.string.get_auth
        CoroutineScope(Dispatchers.IO).launch {
            var answer = ""
            val body: RequestBody = "DeviceID=${guid}".toRequestBody("text/plain".toMediaTypeOrNull())
            while (answer.isEmpty() || answer.contains("<Наименование>0</Наименование>")) {
                val request = networkRequest.getStringResponse(
                    token = appRepository.token,
                    suffix = "web/req/login",
                    body = body
                )
                request.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        answer = if (response.isSuccessful) {
                            response.body()!!.byteStream().bufferedReader().use { it.readText() }
                        } else ""
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        answer = ""
                        authState.value = R.string.no_net
                    }
                })
                delay(1300)
            }
            val jsonData = XML.toJSONObject(answer)
            val jsonObject = jsonData.getJSONObject("ПользовательШД")
            if (jsonObject.getBoolean("РабочееМестоКладовщика")) {
                userInfoRepository.clearUserTable()
                val userInfo = UserInfo(
                    uid = 1,
                    deviceID = jsonObject.getString("ИДУстройства"),
                    name = jsonObject.getString("Наименование"),
                    code = jsonObject.getString("Код"),
                    personCode = "",
                    personName = ""
                )
                userInfoRepository.saveUser(userInfo = userInfo)
                FilesRepository.saveDeviceGUID(GUID = jsonObject.getString("ИДУстройства"))
                prefs.setValue("guid", jsonObject.getString("ИДУстройства"))
                withContext(Main) { authState.value = R.string.auth_ok }
            } else {
                withContext(Main) { authState.value = R.string.auth_error }

            }
        }
    }
}