package vn.vibe.booking.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.vibe.booking.data.local.dao.CategoryDao
import vn.vibe.booking.data.local.dao.ServiceDao
import vn.vibe.booking.data.local.entity.RepairServiceEntity
import vn.vibe.booking.data.local.entity.ServiceCategoryEntity

@Database(
    entities = [ServiceCategoryEntity::class, RepairServiceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun serviceDao(): ServiceDao
}
