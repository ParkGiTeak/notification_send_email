package com.example.notificationsendemail.activity

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.notificationsendemail.databinding.ActivityMainBinding
import com.example.notificationsendemail.service.NotiListenerService
import com.example.notificationsendemail.util.GMailSender

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var requestNotificationListenerPermission: ActivityResultLauncher<Intent>
    private var mGMailSender: GMailSender? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestNotificationListenerPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
          initWidgets()
        }
        mGMailSender = GMailSender("userName", "password")
        initWidgets()
    }

    private fun initWidgets() {
        if (checkPermission()) {
            binding.tvPermissionStatus.text = "PermissionGranted!"
            binding.btnRequestPermission.visibility = View.GONE
        } else {
            binding.tvPermissionStatus.text = "PermissionDenied!"
            binding.btnRequestPermission.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    requestNotificationListenerPermission.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.isNotificationListenerAccessGranted(ComponentName(application, NotiListenerService::class.java))
        } else {
            NotificationManagerCompat.getEnabledListenerPackages(applicationContext).contains(applicationContext.packageName)
        }
    }

    private fun sendMail() {
        mGMailSender?.sendMail("rl980901@naver.com", "Title", "Content")
    }
}