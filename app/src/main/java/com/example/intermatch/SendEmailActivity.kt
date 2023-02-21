package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder.DeathRecipient
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_send_email.*
import org.json.JSONObject

/**
 * Sending an email to faculty
 */
class SendEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)
        supportActionBar?.hide()
        val recipient = intent.getStringExtra("faculty_email")
        val project_name = intent.getStringExtra("project_name")
        val f_name = intent.getStringExtra("faculty_name")
        val user = intent.getStringExtra("username")
        val percent = intent.getStringExtra("match")
        val user_github = intent.getStringExtra("github")
        val user_linkedin = intent.getStringExtra("linkedin")
        edittext_email.text = recipient
        send_btn.setOnClickListener {
            if (recipient != null) {
                sendMail(recipient)
            }

            val volleyQueue = Volley.newRequestQueue(this)
            val url1 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

            val jsonfile1 = JSONObject().apply {
                put("dataSource", "Cluster0")
                put("database", "Intermatch")
                put("collection","Interested")
                put("document",JSONObject().apply {
                    put("project_name",project_name)
                    put("faculty_name",f_name)
                    put("username",user)
                    put("match_percent",percent)
                    put("req_github",user_github)
                    put("req_linkedin", user_linkedin)
                    put("status","Received")
                })
            }

            val request1: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url1, jsonfile1,
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
                    headers.put("Access-Control-Request-Headers", "*");

                    return headers
                }
            }
            volleyQueue.add(request1)
        }
    }

    fun sendMail(recipient: String) {

        var sub : String = edittext_subject.text.toString()
        var msg : String = edittext_msg.text.toString()
        var addresses = arrayOf(recipient)

        var intent : Intent = Intent(Intent.ACTION_SEND)

        intent.putExtra(Intent.EXTRA_EMAIL,addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT,sub)
        intent.putExtra(Intent.EXTRA_TEXT,msg)

        intent.setType("message/rfc822")
        startActivity(Intent.createChooser(intent,"Choose an email client"))


    }
}