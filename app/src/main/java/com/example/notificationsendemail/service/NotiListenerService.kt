package com.example.notificationsendemail.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.notificationsendemail.BuildConfig
import com.example.notificationsendemail.R
import com.example.notificationsendemail.datastore.appPrefDataStore
import com.example.notificationsendemail.datastore.getSavedEmail
import com.example.notificationsendemail.datastore.getSavedPackageList
import com.example.notificationsendemail.datastore.notiPacakgeDataStore
import com.example.notificationsendemail.util.GMailSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotiListenerService : NotificationListenerService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var gMailSender: GMailSender? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("PGT", "onCreate: notiListener")
        val notification = createNotificationForForegroundService()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(100, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(100, notification)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn != null && sbn.packageName != this.packageName) {
            serviceScope.launch {
                notiPacakgeDataStore.getSavedPackageList().collect { savedPackageList ->
                    if (savedPackageList.contains(sbn.packageName)) {
                        val title = sbn.notification.extras.getString(Notification.EXTRA_TITLE)
                        val text = sbn.notification.extras.getString(Notification.EXTRA_TEXT)
                        if (!title.isNullOrEmpty() && !text.isNullOrEmpty()) {
                            Log.d("PGT", "onNotificationPosted: title: $title / text: $text")
                            val gmailId = BuildConfig.GmailId
                            val googleAppPw = BuildConfig.GoogleAppPw
                            if (gMailSender == null) gMailSender =
                                GMailSender(userName = gmailId, password = googleAppPw)
                            appPrefDataStore.getSavedEmail().collect { savedEmail ->
                                gMailSender?.sendMail(savedEmail, title, text)
                            }
                        }
                    } else {
                        Log.e("PGT", "onNotificationPosted: 등록되지 않은 Package의 알림")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotificationForForegroundService(): Notification {
        val channelId = "NotificationListenerChannel"
        val channelName = "알림 감지 서비스"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("알림 감지 중")
            .setContentText("백그라운드에서 알림을 감지하고 있습니다.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}