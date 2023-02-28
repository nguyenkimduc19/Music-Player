package com.example.musicplayerkotlin

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.se.omapi.Session
import android.widget.Toast
import com.example.musicplayerkotlin.databinding.ActivityFeedbackBinding
import java.lang.Exception
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.util.*
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class FeedbackActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "RZ Music Player | Feedback"

        binding.submit.setOnClickListener {
            val feedbackmsg =
                binding.feedback.text.toString() + "\n" + binding.uemail.text.toString()
            val subject = binding.topic.text.toString()
            val userName = "emailAddress@gmail.com"
            val pass = "Password"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (feedbackmsg.isNotEmpty() && subject.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)) {
//                Thread {
//                    try {
//                        val properties = Properties()
//                        properties["mail.smtp.auth"] == "true"
//                        properties["mail.smtp.starttls.enable"] == "true"
//                        properties["mail.smtp.host"] == "smtp.gmail.com"
//                        properties["mail.smtp.port"] == "587"
//
//                        val session = Session.getInstance(properties,object : Authenticator(){
//                            override fun getPasswordAuthentication(): PasswordAuthentication {
//                                return PasswordAuthentication(userName,pass)
//                            }
//                        })
//
//                        val mail = MimeMessage(session)
//                        mail.subject = subject
//                        mail.setText(feedbackmsg)
//                        mail.setFrom(InternetAddress(userName))
//                        mail.setRecipients(Message.RecipientType.TO,InternetAddress.parse(userName))
//                        Transport.send(mail)
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                }.start()
                Toast.makeText(this,"Thanks For Giving Feedback...",Toast.LENGTH_SHORT).show()
                        finish()
            } else {
                Toast.makeText(this, "Something Went Wrong...", Toast.LENGTH_SHORT).show()
            }
        }

    }
}