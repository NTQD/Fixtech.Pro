package vn.vibe.booking

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BookingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
