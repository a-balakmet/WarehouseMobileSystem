package com.example.wms.network

import okhttp3.RequestBody
import javax.inject.Inject

class NetworkRequest @Inject constructor(private val apiService: ApiService) {

    fun getStringResponse(token: String, suffix: String, body: RequestBody) = apiService.initStringRequest(token, suffix, body)

    fun getDateResponse(token: String, suffix: String, body: RequestBody) = apiService.initDateRequest(token, suffix, body)

    fun getTaskResponse(token: String, suffix: String, body: RequestBody) = apiService.initTaskRequest(token, suffix, body)
}