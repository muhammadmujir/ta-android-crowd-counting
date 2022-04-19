package com.final_project.crowd_counting.base.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.final_project.crowd_counting.base.model.entity.PaginationEntity

@Dao
interface PaginationDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(paging: PaginationEntity)

  @Query("SELECT * FROM paginations WHERE id=:id AND country=:country LIMIT 1")
  suspend fun getPagination(id: String, country: String): PaginationEntity?

  @Query("DELETE FROM paginations WHERE id=:id")
  suspend fun clearById(id: String)

  @Query("DELETE FROM paginations")
  suspend fun clearAll()
}