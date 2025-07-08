package com.example.notificationsendemail.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.notificationsendemail.BuildConfig
import com.example.notificationsendemail.datastore.appPrefDataStore
import com.example.notificationsendemail.datastore.getSavedEmail
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
    }
}