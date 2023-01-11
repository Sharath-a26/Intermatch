package com.example.intermatch

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.android.HandlerDispatcher
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.time.LocalDateTime

class WelcomeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()
        val username = intent.getStringExtra("username")
        wel_name.text = "Welcome Back!" + username
        val user_type = intent.getStringExtra("usertype")
        val user_dept = intent.getStringExtra("user_dept")
        Log.d(null,"department = ")
        var intent1 = Intent()

        wel_name.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_out))
        Handler().postDelayed(
            {

                intent1 = Intent(this, RecommendationActivity::class.java)
                intent1.putExtra("username",username)
                intent1.putExtra("usertype",user_type)
                intent1.putExtra("user_dept",user_dept)
                startActivity(intent1)

            },2000
        )



        val url_log = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"
        val curr : LocalDateTime? = LocalDateTime.now()
        Log.d(null,"current time = " + curr.toString())
        val jsonfile_log = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Logs")
            put("document",JSONObject().apply {
                put("username",username)
                put("time",curr.toString())

            })
        }
        val volleyQueue = Volley.newRequestQueue(this)
        val req_log: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url_log, jsonfile_log,
            Response.Listener<JSONObject> { response ->

                                          },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message.toString(), Toast.LENGTH_LONG).show()
            },


            ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json");
                headers.put(
                    "api-key",
                    "52y3eVGzd6zZUik2FCunXVfxWCX4Olar386TTdangtvH1xP0Sunj52wOJxNFqr2K"
                );
                headers.put("Access-Control-Request-Headers", "*");

                return headers
            }
        }


        val MY_SOCKET_TIMEOUT_MS = 50000;
        req_log.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        volleyQueue.add(req_log)
    }
}