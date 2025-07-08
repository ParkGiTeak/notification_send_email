package com.example.notificationsendemail.activity

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.notificationsendemail.databinding.ActivityMainBinding
import com.example.notificationsendemail.datastore.appPrefDataStore
import com.example.notificationsendemail.datastore.saveEmail
import com.example.notificationsendemail.fragment.ApplicationListBottomSheetFragment
import com.example.notificationsendemail.service.NotiListenerService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var requestNotificationListenerPermission: ActivityResultLauncher<Intent>
    private var mApplicationListBottomSheetFragment: ApplicationListBottomSheetFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestNotificationListenerPermission =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                initWidgets()
            }
        initWidgets()
    }

    override fun onDestroy() {
        requestNotificationListenerPermission.unregister()
        super.onDestroy()
        mApplicationListBottomSheetFragment = null
    }

    private fun initWidgets() {
        if (checkPermission()) {
            binding.tvPermissionStatus.visibility = View.GONE
            binding.btnRequestPermission.visibility = View.GONE

            binding.layoutActiveApp.visibility = View.VISIBLE
            binding.btnRequestInstalledApplicationList.setOnClickListener {
                if (mApplicationListBottomSheetFragment == null) {
                    mApplicationListBottomSheetFragment = ApplicationListBottomSheetFragment()
                }
                mApplicationListBottomSheetFragment?.show(
                    supportFragmentManager,
                    "ApplicationListBottomSheetFragment"
                )
            }
            binding.btnRegisterEmail.setOnClickListener {
                val editTextView = EditText(this@MainActivity)
                editTextView.setHint("example@gmail.com")
                val builder = AlertDialog.Builder(this@MainActivity)
                val dialog = builder
                    .setTitle("Email 등록")
                    .setMessage("알림을 받을 Email을 입력하세요.")
                    .setView(editTextView)
                dialog.setPositiveButton("등록", object : DialogInterface.OnClickListener {
                    override fun onClick(
                        dialog: DialogInterface?,
                        which: Int
                    ) {
                        lifecycleScope.launch {
                            Log.d("PGT", "btnRegisterEmail onClick: ${editTextView.text}")
                            appPrefDataStore.saveEmail(editTextView.text.toString())
                        }
                    }
                })
                dialog.show()
            }

            val intent = Intent(this@MainActivity, NotiListenerService::class.java)
            ContextCompat.startForegroundService(this@MainActivity, intent)
        } else {
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
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.isNotificationListenerAccessGranted(
                ComponentName(
                    application,
                    NotiListenerService::class.java
                )
            )
        } else {
            NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
                .contains(applicationContext.packageName)
        }
    }
}