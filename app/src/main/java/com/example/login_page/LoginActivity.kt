package com.example.login_page

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.AuthFailureError
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

            val jsonfile = JSONObject(info)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url, jsonfile,
                Response.Listener<JSONObject> {
                    response ->
                    if (!(response.get("document").equals(null))) {
                        val intent = Intent(this,WelcomeActivity::class.java)
                        intent.putExtra("username",username)
                        startActivity(intent)
                    }


                },
                        Response.ErrorListener { error ->
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();

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



            volleyQueue.add(request);
        }

    }
}