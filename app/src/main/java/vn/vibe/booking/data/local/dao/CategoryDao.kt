package vn.vibe.booking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.vibe.booking.data.local.entity.ServiceCategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM service_categories")
    suspend fun getAllCategories(): List<ServiceCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ServiceCategoryEntity>)

    @Query("DELETE FROM service_categories")
    suspend fun clearCategories()
}
