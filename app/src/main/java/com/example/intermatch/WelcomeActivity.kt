package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.android.HandlerDispatcher
import kotlinx.coroutines.delay

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()
        val username = intent.getStringExtra("username")
        wel_name.text = "Welcome Back!" + username
        val user_type = intent.getStringExtra("usertype")
        var intent1 = Intent()

        wel_name.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_out))
        Handler().postDelayed(
            {

                intent1 = Intent(this, RecommendationActivity::class.java)
                intent1.putExtra("username",username)
                intent1.putExtra("usertype",user_type)
                startActivity(intent1)

            },2000
        )

    }
}