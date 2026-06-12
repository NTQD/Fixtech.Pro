package vn.vibe.booking.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import vn.vibe.booking.data.local.AppDatabase
import vn.vibe.booking.data.local.dao.CategoryDao
import vn.vibe.booking.data.local.dao.ServiceDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fixtech_booking_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideServiceDao(database: AppDatabase): ServiceDao = database.serviceDao()
}
