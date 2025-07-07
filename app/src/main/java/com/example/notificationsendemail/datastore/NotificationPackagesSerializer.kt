package com.example.notificationsendemail.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.notificationsendemail.NotificationPackages
import java.io.InputStream
import java.io.OutputStream

object NotificationPackagesSerializer : Serializer<NotificationPackages> {
    override suspend fun readFrom(input: InputStream): NotificationPackages {
        try {
            return NotificationPackages.parseFrom(input)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(
        t: NotificationPackages,
        output: OutputStream
    ) = t.writeTo(output)

    override val defaultValue: NotificationPackages
        get() = NotificationPackages.getDefaultInstance()
}