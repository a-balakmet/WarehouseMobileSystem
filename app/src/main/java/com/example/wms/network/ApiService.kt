package com.example.wms.network

import com.example.wms.app.models.ServerDate
import com.example.wms.app.models.TaskData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/dbs/{suffix}")
    fun initStringRequest(
        @Header("Authorization") token: String,
        @Path("suffix") suffix: String,
        @Body body: RequestBody
    ): Call<ResponseBody>

    @POST("/dbs/{suffix}")
    fun initDateRequest(
        @Header("Authorization") token: String,
        @Path("suffix") suffix: String,
        @Body body: RequestBody
    ): Call<ServerDate>

    @POST("/dbs/{suffix}")
    fun initTaskRequest(
        @Header("Authorization") token: String,
        @Path("suffix") suffix: String,
        @Body body: RequestBody
    ): Call<TaskData>
}