package com.example.notificationsendemail.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.notificationsendemail.NotificationPackages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

private const val notificationPackageDataStoreName = "notification_package.pb"
private const val appPrefDataStoreName = "notification_send_preference_data_store"

val Context.notiPacakgeDataStore: DataStore<NotificationPackages> by dataStore(
    fileName = notificationPackageDataStoreName,
    serializer = NotificationPackagesSerializer
)

val Context.appPrefDataStore: DataStore<Preferences> by preferencesDataStore(name = appPrefDataStoreName)

suspend fun DataStore<NotificationPackages>.addPackageList(packageName: String) {
    this.updateData { notificationPackages ->
        notificationPackages.toBuilder()
            .addPackage(packageName)
            .build()
    }
}

suspend fun DataStore<NotificationPackages>.removePackage(packageName: String) {
    /*
    * ProtoBuf에서 repeat(List)는 단일 객체를 삭제하는 remove같은 메서드를 제공하지 않아서
    * 전체리스트에서 지울거 지우고 clear해주고 다시 더해줘야한다.
    */
    this.updateData { notificationPackages ->
        val currentPackageList = notificationPackages.packageList.toMutableList()
        currentPackageList.remove(packageName)
        notificationPackages.toBuilder()
            .clearPackage()
            .addAllPackage(currentPackageList)
            .build()
    }
}

fun DataStore<NotificationPackages>.getSavedPackageList(): Flow<List<String>> =
    this.data
        .catch { e ->
            if (e is IOException) {
                emit(NotificationPackages.getDefaultInstance())
            } else {
                throw e
            }
        }
        .map { notificationPackages ->
            notificationPackages.packageList
        }
        .distinctUntilChanged()

suspend fun DataStore<Preferences>.saveEmail(email: String) {
    this.edit { dataStore ->
        dataStore[PreferenceKeys.emailPreferenceKey] = email
    }
}

fun DataStore<Preferences>.getSavedEmail(): Flow<String> =
    this.data
        .catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { dataStore ->
            dataStore[PreferenceKeys.emailPreferenceKey] ?: ""
        }
        .distinctUntilChanged()