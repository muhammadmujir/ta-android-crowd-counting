package com.final_project.crowd_counting.base.source.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.final_project.crowd_counting.base.model.entity.ProductRequestEntity
import com.final_project.crowd_counting.base.model.relation.ProductRequestAndCustomerAlsoTraveller
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductRequestDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(productRequests: List<ProductRequestEntity>)

  @Transaction
  @Query("SELECT * FROM product_requests WHERE country=:country")
  fun pagingSource(country: String): PagingSource<Int, ProductRequestAndCustomerAlsoTraveller>

  @Query("SELECT * FROM product_requests")
  fun getProductRequestList(): Flow<List<ProductRequestEntity>>

  @Query("DELETE FROM product_requests WHERE country=:country")
  suspend fun deleteByCountry(country: String)

  @Query("DELETE FROM product_requests")
  suspend fun clearAll()

}