package com.example.valetparking

import android.os.AsyncTask
import android.util.Log
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendMail(
    private val senderEmail: String,
    private val senderPass: String,
    private val email: String,
    private val subject: String,
    private val message: String
) : AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg voids: Void?): Void? {
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPass)
            }
        })

        try {
            // Create MimeMessage object
            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail))
                addRecipient(Message.RecipientType.TO, InternetAddress(email))
                this.subject = this@SendMail.subject
                setText(this@SendMail.message)
            }

            // Send the message
            Transport.send(mimeMessage)
            Log.d("SendMail", "Email sent successfully")
        } catch (e: MessagingException) {
            Log.e("SendMail", "Error sending email: ${e.message}")
        }
        return null
    }
}
