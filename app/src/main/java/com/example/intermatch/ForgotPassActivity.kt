package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_forgot_pass.*
import org.json.JSONObject

class ForgotPassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)
        supportActionBar?.hide()
        reset_btn.setOnClickListener {


            if (new_password.text.toString().equals(conf_password.text.toString())) {

                val volleyQueue = Volley.newRequestQueue(this)
                val url_reset =
                    "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/updateOne"
                var off_email = off_email.text.toString()
                var new_pass = new_password.text.toString()
                var json_reset = JSONObject().apply {
                    put("dataSource", "Cluster0")
                    put("database", "Intermatch")
                    put("collection", "User")
                    put("filter", JSONObject().apply {
                        put("email", off_email)

                    })

                    put("update", JSONObject().apply {
                        put("$" + "set", JSONObject().apply {
                            put("password", new_pass)
                        })
                    })
                }

                val request_reset: JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url_reset, json_reset,
                    Response.Listener<JSONObject> { response ->
                        //Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
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
                        headers.put("Access-Control-Request-Headers", "*");

                        return headers
                    }
                }
                volleyQueue.add(request_reset)

                startActivity(Intent(this,LoginActivity::class.java))
            }
            else{
                Toast.makeText(this,"Passwords do not match",Toast.LENGTH_LONG).show()
            }
        }
    }
}