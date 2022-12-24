package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        regButton.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)

            startActivity(intent)
        }

        loginButton.setOnClickListener {

            val volleyQueue = Volley.newRequestQueue(this)
            val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"

            val username = username.text.toString()
            val password = password.text.toString()

            val info = """
                {
                "dataSource": "Cluster0",
                "database": "Intermatch",
                "collection": "User",
                "filter" : {"username" : $username, "password" : $password}
                }
            """.trimIndent()

            val jsonfile = JSONObject().apply {
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","User")
                put("filter",
                JSONObject().apply {
                    put("username",username)
                    put("password",password)
                })
            }
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url, jsonfile,
                Response.Listener<JSONObject> {
                    response ->
                    if (!(response.get("document").equals(null))) {
                        val intent = Intent(this,WelcomeActivity::class.java)
                        val user_type = response.getJSONObject("document").get("Type").toString()
                        Log.d(null,user_type)
                        intent.putExtra("username",username)
                        intent.putExtra("usertype",user_type)
                        startActivity(intent)
                    }

                    else {
                        val toast = Toast(this)

                        Toast.makeText(this,"Invalid Username or password",Toast.LENGTH_LONG).show()
                    }


                },
                        Response.ErrorListener { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show();

                }
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



            volleyQueue.add(request);
        }

    }
}