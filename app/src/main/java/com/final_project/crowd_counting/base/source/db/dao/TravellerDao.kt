package com.final_project.crowd_counting.base.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.final_project.crowd_counting.base.model.entity.TravellerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TravellerDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(traveller: TravellerEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(travellerList: List<TravellerEntity>)

  @Query("SELECT * FROM travellers LIMIT 1")
  fun getTraveller(): Flow<TravellerEntity>

  @Query("DELETE FROM travellers")
  suspend fun clearAll()
}