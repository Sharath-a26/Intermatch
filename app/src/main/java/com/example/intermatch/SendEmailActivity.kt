package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder.DeathRecipient
import kotlinx.android.synthetic.main.activity_send_email.*

/**
 * Sending an email to faculty
 */
class SendEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)
        supportActionBar?.hide()
        val recipient = intent.getStringExtra("faculty_email")
        edittext_email.text = recipient
        send_btn.setOnClickListener {
            if (recipient != null) {
                sendMail(recipient)
            }
        }
    }

    fun sendMail(recipient: String) {

        var sub : String = edittext_subject.text.toString()
        var msg : String = edittext_msg.text.toString()
        var addresses = arrayOf(recipient)

        var intent : Intent = Intent(Intent.ACTION_SEND)

        intent.putExtra(Intent.EXTRA_EMAIL,addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT,sub)
        intent.putExtra(Intent.EXTRA_TEXT,msg)

        intent.setType("message/rfc822")
        startActivity(Intent.createChooser(intent,"Choose an email client"))


    }
}