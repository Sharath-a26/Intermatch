package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_upload_user_details.*
import org.json.JSONArray
import org.json.JSONObject

class UploadUserDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_user_details)
        supportActionBar?.hide()
        val username = intent.getStringExtra("username")
        val user_email = intent.getStringExtra("user_email")
        val user_type = intent.getStringExtra("usertype")



        next_btn.setOnClickListener{
            if(user_type == "Faculty") {
                val intent : Intent = Intent(this@UploadUserDetails,UploadProjectActivity::class.java)
                intent.putExtra("faculty_email",user_email)
                intent.putExtra("faculty_name",username) //faculty's username
                startActivity(intent)
            }
            else {
                val intent : Intent = Intent(this@UploadUserDetails,UploadInterestActivity::class.java)
                intent.putExtra("username",username)

                startActivity(intent)
            }

            val volleyQueue = Volley.newRequestQueue(this)
            val url1 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/updateOne"
            val a = JSONObject().apply {
                put("dataSource", "Cluster0")
                put("database", "Intermatch")
                put("collection", "User")
                put("filter",JSONObject().apply {
                    put("username",username)
                })
                put("update",JSONObject().apply {
                    put("$"+"set",JSONObject().apply {
                        put("aboutme",aboutme.text)
                        put("github",github.text)
                        put("linkedin",linked_in.text)
                    })
                })
            }

            val request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url1, a,
                Response.Listener<JSONObject> { response ->
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.message.toString(), Toast.LENGTH_LONG).show();

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
            volleyQueue.add(request)
        }
    }
}