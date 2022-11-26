package com.example.intermatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.username
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        profile_layout.isVisible = false
        val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
        val volleyQueue = Volley.newRequestQueue(this)
        val jsonfile = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","User")
            put("filter",JSONObject().apply {
                put("username",intent.getStringExtra("username"))
            })
        }

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url, jsonfile,
            Response.Listener<JSONObject> { response ->

                       profile_user.text = response.getJSONObject("document").get("username").toString()
                        //profile_type.text = response.getJSONObject("document").get("Type").toString()
                        profile_email.text = response.getJSONObject("document").get("email").toString()
                        profile_pass.text = response.getJSONObject("document").get("password").toString()
                        profile_layout.isVisible = true

            },
            Response.ErrorListener { error ->
                profile_user.text = "error"
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
                headers.put("Access-Control-Request-Headers","*");

                return headers
            }
        }


        val MY_SOCKET_TIMEOUT_MS = 50000;
        request.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        volleyQueue.add(request)
    }
}
