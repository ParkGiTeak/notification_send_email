package com.example.notificationsendemail.util

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
class GMailSender(private val userName: String, private val password: String) : Authenticator() {
    private var session: Session

    init {
        val properties = Properties().apply {
            this.setProperty("mail.transport.protocol", "smtp")
            this.setProperty("mail.host", "smtp.gmail.com")
            this.setProperty("mail.smtp.auth", "true")
            this.setProperty("mail.smtp.port", "587")
            this.setProperty("mail.smtp.quitwait", "false")
            this.setProperty("mail.smtp.starttls.enable", "true")
            this.setProperty("mail.smtp.ssl.protocols", "TLSv1.2")
        }
        session = Session.getInstance(properties, this)
    }

    // userName과 password로 전송 계정 확인
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(userName, password)
    }

    fun sendMail(recipient: String, title: String, content: String?) {
        val message = MimeMessage(session)
        message.apply {
            this.sender = InternetAddress(userName)
            this.addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
            this.subject = title
            this.setText(content ?: "")
        }
        Transport.send(message)
    }
}