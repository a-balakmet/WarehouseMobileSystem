package com.example.wms.app.models

data class ProductToCompare(
    val SoapEnvelopeProduct: SoapEnvelopeProduct
)

data class SoapEnvelopeProduct(
    val SoapBodyProduct: SoapBodyProduct,
    val xmlnssoap: String
)

data class SoapBodyProduct(
    val GetSeriesByCodeResponse: GetSeriesByCodeResponse
)

data class GetSeriesByCodeResponse(
    val TheReturnProduct: TheReturnProduct,
    val xmlnsm: String
)

data class TheReturnProduct(
    val ProductSeries: List<ProductSeries>,
    val type: String,
    val xmlnsxs: String,
    val xmlnsxsi: String
)

data class ProductSeries(
    val seriesNo: String,
    val productionDate: String
)
