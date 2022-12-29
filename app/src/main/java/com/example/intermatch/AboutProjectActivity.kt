package com.example.intermatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_about_project.*
import org.json.JSONObject
var requested_users = ArrayList<String>()
var match_users  =  ArrayList<Int>()
var gits = ArrayList<String>()
var linkedin_list = ArrayList<String>()
class AboutProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_project)
        supportActionBar?.hide()
        val this_prj = intent.getStringExtra("prjname")
        val faculty_name = intent.getStringExtra("faculty_name")

        val req_listview = findViewById<ListView>(R.id.prj_request_list)
        var reqadapter : RequestAdapter

        val volleyQueue = Volley.newRequestQueue(this)
        val url_stud_reqs = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"

        val jsonfile_stud_reqs = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Interested")
            put("filter",JSONObject().apply {
                put("faculty_name",faculty_name)
                put("project_name",this_prj)
            })
        }

        val request_stud_reqs : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url_stud_reqs, jsonfile_stud_reqs,
            Response.Listener<JSONObject> {response ->
                for (i in 0 until response.getJSONArray("documents").length()) {
                    requested_users.add(response.getJSONArray("documents").getJSONObject(i).get("username").toString())
                    match_users.add(response.getJSONArray("documents").getJSONObject(i).get("match_percent").toString().toInt())
                    gits.add(response.getJSONArray("documents").getJSONObject(i).get("req_github").toString())
                    linkedin_list.add(response.getJSONArray("documents").getJSONObject(i).get("req_linkedin").toString())
                }

                if (this_prj != null && faculty_name != null) {
                    reqadapter = RequestAdapter(
                        this.baseContext, requested_users, match_users, gits,
                        linkedin_list, this_prj, faculty_name
                    )
                    req_listview.adapter = reqadapter
                }

            },
            Response.ErrorListener { error ->

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
        request_stud_reqs.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        volleyQueue.add(request_stud_reqs)



    }
}