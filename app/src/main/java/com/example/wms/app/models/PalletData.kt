package com.example.wms.app.models

import com.example.wms.room.entities.Cells

data class PalletData(
    var index: Int,
    var palletBarcode: String,
    var productArticle: String,
    var productName: String,
    var productSeries: String,
    var productDate: String,
    var productQuantity: String,
    var cellFrom: Cells?,
    var cellTo: Cells?,
    var operationID: String,
)
