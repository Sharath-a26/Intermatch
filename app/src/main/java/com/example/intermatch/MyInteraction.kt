package com.example.intermatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

var my_req_prjs = ArrayList<String>()
var my_req_faculties = ArrayList<String>()
var my_req_stats = ArrayList<String>()
class MyInteraction : AppCompatActivity() {
    var mylist = ArrayList<String>()
    var faclist = ArrayList<String>()
    var statlist = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_interaction)
        supportActionBar?.hide()

        val inter_listview = findViewById<ListView>(R.id.my_interact_list)
        /**
         * listing all the requests by the user
         * is updated when the faculty accepts it
         */
        val username_stat = intent.getStringExtra("username")
        val volleyQueue = Volley.newRequestQueue(this)

        val url_status = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"
        val jsonfile_stat = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Interested")
            put("filter", JSONObject().apply {
                put("username",username_stat)
            })
        }

        val request_stat : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url_status, jsonfile_stat,
            Response.Listener<JSONObject> { response ->

                for (i in 0 until response.getJSONArray("documents").length()) {
                    my_req_prjs.add(response.getJSONArray("documents").getJSONObject(i).get("project_name").toString())

                    my_req_faculties.add(response.getJSONArray("documents").getJSONObject(i).get("faculty_name").toString())
                    my_req_stats.add(response.getJSONArray("documents").getJSONObject(i).get("status").toString())
                }
                for (i in 0 until my_req_prjs.size) {
                    if (!(my_req_prjs.get(i) in mylist)) {
                        mylist.add(my_req_prjs.get(i))
                    }

                    if (!(my_req_faculties.get(i) in faclist)) {
                        faclist.add(my_req_faculties.get(i))
                    }

                    if (!(my_req_stats.get(i) in statlist)) {
                        statlist.add(my_req_stats.get(i))
                    }
                }

                var inter_adapter = InteractionAdapter(applicationContext,mylist,faclist,statlist)
                inter_listview.adapter = inter_adapter

                Log.d(null,mylist.toString())



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
        request_stat.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        volleyQueue.add(request_stat)



    }
}