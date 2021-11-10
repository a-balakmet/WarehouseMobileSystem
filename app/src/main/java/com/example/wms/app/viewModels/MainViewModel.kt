package com.example.wms.app.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import com.example.wms.R
import com.example.wms.app.models.StorekeeperTask
import com.example.wms.app.models.TaskData
import com.example.wms.app.repositories.AppRepository
import com.example.wms.app.repositories.FilesRepository
import com.example.wms.app.repositories.NetworkRepository.checkConnectionState
import com.example.wms.app.sharedPrefs.PreferenceRepository
import com.example.wms.network.NetworkRequest
import com.example.wms.room.entities.UserInfo
import com.example.wms.room.repositories.PersonnelRepository
import com.example.wms.room.repositories.UserInfoRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val prefs: PreferenceRepository,
    private val userInfoRepository: UserInfoRepository,
    private val personnelRepository: PersonnelRepository,
    private val networkNetworkRequest: NetworkRequest,
): ViewModel() {

    var userInfo: MutableState<UserInfo?> = mutableStateOf(null)

    var theBarcode: MutableState<String?> = mutableStateOf(null)
    var messageToShow: MutableState<String?> = mutableStateOf(null)
    var userName: MutableState<String> = mutableStateOf("")

    var tasksLoadingState: MutableState<Int?> = mutableStateOf(null)
    private lateinit var taskNoLocal: TaskData
    private lateinit var taskLocal: TaskData
    var taskLoadingProgress = mutableStateOf(0)
    var movementTasks: MutableState<MutableList<StorekeeperTask>?> = mutableStateOf(null)
    var loadingErrors: MutableState<String?> = mutableStateOf(null)
    var task: MutableState<StorekeeperTask?> = mutableStateOf(null)

    fun loadInitUserData() {
        CoroutineScope(IO).launch { userInfo.value = userInfoRepository.getUser() }
    }

    fun dropAuthentication() {
        val guid = UUID.randomUUID().toString()
        CoroutineScope(IO).launch {
            FilesRepository.deleteSavedGuid()
            prefs.setValue("guid", guid)
        }
        restartApp()
    }

    fun openDateSettings() = appRepository.openDateSettings()

    fun restartApp() = appRepository.restartApp()

    fun closeApp() = appRepository.closeApp()

    fun openNetworkSettings(isStart: Boolean) = appRepository.openNetworkSettings(isStart)

    fun openBluetoothSettings() = appRepository.openBluetoothSettings()

    fun findUser(barcode: String) {
        CoroutineScope(IO).launch {
            personnelRepository.getPerson(barcode = barcode)?.let {
                userName.value = it.name
                val user = userInfoRepository.getUser()
                userInfoRepository.clearUserTable()
                val newUser = UserInfo(
                    uid = 1,
                    deviceID = user.deviceID,
                    name = user.name,
                    code = it.code,
                    personCode = barcode,
                    personName = it.name
                )
                userInfo.value = newUser
                userInfoRepository.saveUser(userInfo = newUser)
                messageToShow.value = appRepository.getStringFromInt(intValue = R.string.choose_operation)
                playSound(R.raw.info)
            }
        }
    }

    fun playSound(sound: Int) {
        appRepository.playSound(sound)
    }

    fun loadTasks(){
        tasksLoadingState.value = R.string.loading
        CoroutineScope(IO).launch {
            if(checkConnectionState()) {
                val loadingTasks = arrayListOf("/web/req/gettasksglobal", "/web/req/gettasklocal")
                val body: RequestBody = "DeviceID=${userInfo.value!!.deviceID}&PersonID=${userInfo.value!!.code}".toRequestBody("text/plain".toMediaTypeOrNull())
                for (aTask in loadingTasks) {
                    val request = networkNetworkRequest.getTaskResponse(
                        token = appRepository.token,
                        suffix = aTask,
                        body = body
                    )
                    request.enqueue(object : Callback<TaskData>{
                        override fun onResponse(call: Call<TaskData>, response: Response<TaskData>) {
                            if (aTask.contains("local")) {
                                taskLocal = response.body()!!
                            } else {
                                taskNoLocal = response.body()!!
                            }
                            taskLoadingProgress.value += 1
                        }

                        override fun onFailure(call: Call<TaskData>, t: Throwable) {
                            taskLoadingProgress.value -= 1
                            playSound(R.raw.error)
                            tasksLoadingState.value = R.string.error_at_task_loading
                            loadingErrors.value = "$aTask: ${loadingErrors.value}\n$t"
                        }
                    })
                }
            } else {
                tasksLoadingState.value = R.string.no_server_connection
            }
        }
    }

    fun mergeTasks(){
        val firstList = taskNoLocal.StorekeeperTask.filter { it.status == "КОтбору" || it.status == "В работе" }
        val secondList = taskLocal.StorekeeperTask.filter { it.status == "КОтбору" || it.status == "В работе" }
        merge(firstList, secondList).apply {
            if (this.size == 0) tasksLoadingState.value = R.string.no_tasks
            else {
                tasksLoadingState.value = R.string.loading_finished
                movementTasks.value = this
                taskLoadingProgress.value = 0
            }
        }
    }

    private fun <T> merge(first: List<T>, second: List<T>): MutableList<T> {
        val list: MutableList<T> = ArrayList(first)
        list.addAll(second)
        return list
    }
}