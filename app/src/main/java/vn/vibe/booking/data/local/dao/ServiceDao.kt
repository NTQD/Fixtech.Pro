package vn.vibe.booking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.vibe.booking.data.local.entity.RepairServiceEntity

@Dao
interface ServiceDao {
    @Query("SELECT * FROM repair_services")
    suspend fun getAllServices(): List<RepairServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<RepairServiceEntity>)

    @Query("DELETE FROM repair_services")
    suspend fun clearServices()
}
