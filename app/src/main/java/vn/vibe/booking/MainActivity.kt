package vn.vibe.booking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vn.vibe.booking.core.di.AppContainer
import vn.vibe.booking.presentation.app.BookingApp

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookingApp(appContainer = appContainer)
        }
    }
}