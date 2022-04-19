package com.final_project.crowd_counting.base.source.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.final_project.crowd_counting.base.model.entity.ProductRequestCountEntity
import com.final_project.crowd_counting.base.model.entity.ProductRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductRequestCountDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(productRequestCount: ProductRequestCountEntity)

  @Query("SELECT * FROM product_request_count WHERE country=:country")
  fun getProductRequest(country: String): Flow<ProductRequestCountEntity>

  @Query("DELETE FROM product_request_count")
  suspend fun clearAll()
}