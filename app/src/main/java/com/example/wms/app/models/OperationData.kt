package com.example.wms.app.models

data class OperationData(
    val date: String,
    val organizationCode: String,
    val stockFrom: String,
    val stockTo: String,
    var palletsQuantity: Int,
    val deviceId: String,
    val agentCode: String,
    val type: Int,
    val uniqueID: String,
    val uniqueTaskId: String,
    var palletData: List<PalletData>?
)
