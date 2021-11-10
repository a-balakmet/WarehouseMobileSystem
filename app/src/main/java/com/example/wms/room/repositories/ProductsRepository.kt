package com.example.wms.room.repositories

import com.example.wms.room.daos.ProductDao
import com.example.wms.room.entities.Product
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val productDao: ProductDao) {

    suspend fun saveProducts(products: ArrayList<Product>) {
        for (product in products) {
            productDao.insert(product)
        }
    }

    suspend fun clearProducts() {
        productDao.clearProducts()
    }

    fun getProductByArticle(art: String) = productDao.findProductByArticle(art = art)

    fun getProductByBarcode(code: String) = productDao.findProductByBarcode(code = code)
}