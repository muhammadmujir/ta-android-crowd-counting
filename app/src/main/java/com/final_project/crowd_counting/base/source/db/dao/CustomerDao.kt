package com.final_project.crowd_counting.base.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.final_project.crowd_counting.base.model.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(customer: CustomerEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(customerList: List<CustomerEntity>)

  @Query("SELECT * FROM customers WHERE id=:id")
  fun getCustomer(id: String): Flow<CustomerEntity>

  @Query("DELETE FROM customers")
  suspend fun clearAll()
}