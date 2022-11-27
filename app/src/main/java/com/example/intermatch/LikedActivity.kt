package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class LikedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked)
        supportActionBar?.hide()
        val username = intent.getStringExtra("username")
        val like_view : BottomNavigationView = findViewById<BottomNavigationView>(R.id.navView)
        like_view.selectedItemId = R.id.item2
        like_view.setOnItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.item1 -> startActivity(Intent(this,RecommendationActivity::class.java)
                        .putExtra("username",username))
                    R.id.item2 -> startActivity(Intent(this,LikedActivity::class.java)
                        .putExtra("username",username))

                    R.id.item3 -> startActivity(Intent(this,ProfileActivity::class.java)
                        .putExtra("username",username))
                }
                true
            }
        )
    }
}