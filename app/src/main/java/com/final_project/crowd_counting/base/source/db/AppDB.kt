package com.final_project.crowd_counting.base.source.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.final_project.crowd_counting.base.model.entity.*
import com.final_project.crowd_counting.base.source.db.dao.*

@Database(
  entities = [
    ProductRequestEntity::class,ProductRequestCountEntity::class,
    ProductEntity::class,ProductCountEntity::class,OrderCountEntity::class,TravellerEntity::class,CustomerEntity::class,
    PaginationEntity::class, OrderEntity::class, OrderedProductEntity::class
  ],
  version = 1, exportSchema = false
)
abstract class AppDB: RoomDatabase() {

  abstract fun productRequestDao(): ProductRequestDao
  abstract fun productRequestCountDao(): ProductRequestCountDao
  abstract fun productCountDao(): ProductCountDao
  abstract fun productDao(): ProductDao
  abstract fun orderCountDao(): OrderCountDao
  abstract fun orderDao(): OrderDao
  abstract fun orderedProductDao(): OrderedProductDao
  abstract fun travellerDao(): TravellerDao
  abstract fun customerDao(): CustomerDao
  abstract fun paginationDao(): PaginationDao

  companion object {
    @Volatile private var instance: AppDB? = null

    fun getDatabase(context: Context): AppDB =
      instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

    private fun buildDatabase(appContext: Context) =
      Room.databaseBuilder(appContext, AppDB::class.java, "fastipDb")
        .fallbackToDestructiveMigration()
        .build()
  }
}

const val PAGING_PRODUCT_KEY = "product"
const val PAGING_PRODUCT_REQUEST_KEY = "product_request"
const val PAGING_ORDER_KEY = "order"