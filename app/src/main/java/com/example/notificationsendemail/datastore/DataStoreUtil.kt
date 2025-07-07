package com.example.notificationsendemail.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.notificationsendemail.NotificationPackages

private const val notificationPackageDataStoreName = "notification_package.pb"

val Context.notiPacakgeDataStore: DataStore<NotificationPackages> by dataStore(
    fileName = notificationPackageDataStoreName,
    serializer = NotificationPackagesSerializer
)