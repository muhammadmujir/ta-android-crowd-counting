package com.final_project.crowd_counting.base.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.final_project.crowd_counting.base.model.entity.ProductCountEntity
import com.final_project.crowd_counting.base.model.entity.ProductRequestCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCountDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(productCount: ProductCountEntity)

  @Query("SELECT * FROM product_count WHERE prod_count_country=:country")
  fun getProductCount(country: String): Flow<ProductCountEntity>

  @Query("DELETE FROM product_count")
  suspend fun clearAll()
}