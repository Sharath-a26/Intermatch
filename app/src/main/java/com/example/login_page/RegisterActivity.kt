package com.example.login_page

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_register.*


import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        signin.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        backbutton.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        signButton.setOnClickListener {
            val volleyQueue = Volley.newRequestQueue(this)
            val url =
                "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

            var username = username.text.toString()
            var user_email = email.text.toString()
            var password = password.text.toString()

            val doc = """
                {
        "username":$username,
        "email" : $user_email,
        "password":$password
      }
        
            """.trimIndent()
            val docfile = JSONObject(doc)
            if ("students" in user_email) {
                docfile.put("Type","Student")
            }
            else {
                docfile.put("Type","Faculty")
            }
            val info = """
                {
      "dataSource": "Cluster0",
      "database": "Intermatch",
      "collection": "User",
      "document": ${docfile.toString()}
  } """.trimIndent()
            val jsonfile = JSONObject(info)

            val request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url, jsonfile,
                Response.Listener<JSONObject> { response ->
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();

                }) {



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
            //changing the default timeout to be 50000ms in volley
            val MY_SOCKET_TIMEOUT_MS = 50000;
            request.setRetryPolicy(
                DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            volleyQueue.add(request);


        }
    }
}