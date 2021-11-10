package com.example.wms.app.models

import com.google.gson.annotations.SerializedName

data class TaskData(
    @SerializedName("ЗаданиеКладовщику")
    var StorekeeperTask: List<StorekeeperTask>
)

data class StorekeeperTask(
    @SerializedName("Ref_Key")
    val refKey: String,
    @SerializedName("Дата")
    val date: String,
    @SerializedName("Задание")
    val ProductToMove: ArrayList<ProductToMove>,
    @SerializedName("Номер")
    val number: String,
    @SerializedName("Проверка")
    val isCheck: Boolean,
    @SerializedName("СкладКуда")
    val stockToName: String,
    @SerializedName("СкладКудаКод")
    val stockToCode: String,
    @SerializedName("СкладКудаШК")
    val stockToBarcode: String,
    @SerializedName("СкладОткуда")
    val stockFrom: String,
    @SerializedName("СкладОткудаКод")
    val stockFromCode: String,
    @SerializedName("СкладОткудаШК")
    val stockFromBarcode: String,
    @SerializedName("Состояние")
    val status: String,
    @SerializedName("ТипЗадания")
    val type: String
)

data class ProductToMove(
    @SerializedName("ГоденДо")
    val validUntil: String,
    @SerializedName("Количество")
    var quantity: Int,
    @SerializedName("КоличествоУпаковок")
    val packsQuantity: Int,
    @SerializedName("Номенклатура")
    val productName: String,
    @SerializedName("НоменклатураАртикул")
    val productArticle: String,
    @SerializedName("НоменклатураКод")
    val productCode: String,
    @SerializedName("Серия")
    val productSeries: String,
    @SerializedName("Упаковка")
    val packageType: String,
    @SerializedName("Ячейка")
    val cellName: String,
    @SerializedName("ЯчейкаКуда")
    val cellToName: String,
    @SerializedName("ЯчейкаКудаШК")
    val cellToBarcode: String,
    @SerializedName("ЯчейкаШК")
    val cellBarcode: String
)