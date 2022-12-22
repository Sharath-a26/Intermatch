package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.android.HandlerDispatcher
import kotlinx.coroutines.delay

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        wel_name.text = intent.getStringExtra("username")
        val user_type = intent.getStringExtra("usertype")
        var intent1 = Intent()
        Handler().postDelayed(
            {
                intent1 = Intent(this, RecommendationActivity::class.java)
                intent1.putExtra("username",wel_name.text)
                intent1.putExtra("usertype",user_type)
                startActivity(intent1)

            },2000
        )

    }
}