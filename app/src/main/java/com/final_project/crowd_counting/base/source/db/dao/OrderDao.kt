package com.final_project.crowd_counting.base.source.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.final_project.crowd_counting.base.model.entity.OrderEntity
import com.final_project.crowd_counting.base.model.relation.OrderAndCustomerWithOrderedProducts

@Dao
interface OrderDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(orders: List<OrderEntity>)

  @Transaction
  @Query("SELECT * FROM orders WHERE country=:country AND status=:status")
  fun pagingSource(country: String, status: String): PagingSource<Int, OrderAndCustomerWithOrderedProducts>

  @Query("DELETE FROM orders WHERE country=:country AND status=:status")
  suspend fun deleteByCountryAndStatus(country: String, status: String)

  @Query("DELETE FROM orders")
  suspend fun clearAll()
}