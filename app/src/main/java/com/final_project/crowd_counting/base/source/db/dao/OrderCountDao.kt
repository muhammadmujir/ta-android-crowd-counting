package com.final_project.crowd_counting.base.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.final_project.crowd_counting.base.model.entity.OrderCountEntity
import com.final_project.crowd_counting.base.model.entity.ProductCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderCountDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(orderCount: OrderCountEntity)

  @Query("SELECT * FROM order_count WHERE order_count_country=:country")
  fun getOrderCount(country: String): Flow<OrderCountEntity>

  @Query("DELETE FROM order_count")
  suspend fun clearAll()
}