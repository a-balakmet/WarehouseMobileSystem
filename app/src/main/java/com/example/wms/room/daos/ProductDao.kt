package com.example.wms.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wms.room.entities.Product

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: Product)

    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Query("SELECT * FROM products WHERE article = :art")
    fun findProductByArticle(art: String) : Product?

    @Query("SELECT * FROM products WHERE barcode = :code")
    fun findProductByBarcode(code: String) : Product?
}