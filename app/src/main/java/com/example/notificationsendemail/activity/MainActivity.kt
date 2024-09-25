package com.example.notificationsendemail.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationsendemail.databinding.ActivityMainBinding
import com.example.notificationsendemail.util.GMailSender

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mGMailSender: GMailSender? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mGMailSender = GMailSender("userName", "password")
        initWidgets()
    }

    private fun initWidgets() {
        binding.btnSendEmail.setOnClickListener {
            sendMail()
        }
    }

    private fun sendMail() {
        mGMailSender?.sendMail("rl980901@naver.com", "Title", "Content")
    }
}