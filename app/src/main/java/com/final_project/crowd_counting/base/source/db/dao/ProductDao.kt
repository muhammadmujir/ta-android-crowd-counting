package com.final_project.crowd_counting.base.source.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.final_project.crowd_counting.base.model.entity.ProductEntity
import com.final_project.crowd_counting.base.model.relation.CountWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(products: List<ProductEntity>)

  @Transaction
  @Query("SELECT * FROM product_count JOIN products ON product_count.prod_count_country = products.country WHERE country=:country LIMIT 10")
  fun getCountWithProductList(country: String): Flow<CountWithProducts>

  @Query("SELECT * FROM products WHERE country=:country")
  fun getProductListPagingSource(country: String): PagingSource<Int, ProductEntity>

  @Query("DELETE FROM products WHERE country=:country")
  suspend fun deleteByCountry(country: String)

  @Query("DELETE FROM products")
  suspend fun clearAll()
}