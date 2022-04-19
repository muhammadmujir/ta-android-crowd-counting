package com.final_project.crowd_counting.base.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.final_project.crowd_counting.base.model.entity.OrderedProductEntity

@Dao
interface OrderedProductDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(products: List<OrderedProductEntity>)
}