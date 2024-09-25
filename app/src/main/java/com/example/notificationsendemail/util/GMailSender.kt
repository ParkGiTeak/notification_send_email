package com.example.notificationsendemail.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * userName은 Google Gmail 계정 password는 앱 비밀번호
 */
class GMailSender(private val userName: String, private val password: String): Authenticator() {
    private val mailHost = "smtp.gmail.com"
    private var session: Session

    init {
        val properties = Properties()
        properties.apply {
            this.setProperty("mail.transport.protocol", "smtp")
            this.setProperty("mail.host", mailHost)
            this["mail.smtp.auth"] = "true"
            this["mail.smtp.port"] = "465"
            this["mail.smtp.socketFactory.port"] = "465"
            this["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            this["mail.smtp.socketFactory.fallback"] = "false"
            this.setProperty("mail.smtp.quitwait", "false")
        }
        session = Session.getDefaultInstance(properties, this)
    }

    // userName과 password로 전송 계정 확인
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(userName, password)
    }

    fun sendMail(recipient: String, title: String, content: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val message = MimeMessage(session)
            message.apply {
                this.sender = InternetAddress(userName)
                this.addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                this.subject = title
                content?.let {
                    this.setText(it)
                }
            }
            Transport.send(message)
        }
    }
}