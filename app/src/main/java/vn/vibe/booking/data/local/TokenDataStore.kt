package vn.vibe.booking.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import vn.vibe.booking.domain.repository.TokenRepository
import java.io.IOException

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenDataStore @Inject constructor(
    @ApplicationContext context: Context
) : TokenRepository {

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("auth.preferences_pb") }
    )

    override val token: Flow<String?> = dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences()) else throw it
        }
        .map { it[TOKEN_KEY] }

    override suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    override suspend fun clearToken() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }

    override suspend fun getCurrentToken(): String? = token.firstOrNull()

    private companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
    }
}