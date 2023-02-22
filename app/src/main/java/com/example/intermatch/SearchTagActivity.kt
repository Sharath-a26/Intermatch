package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class SearchTagActivity : AppCompatActivity() {
    lateinit var listview : ListView
    var tagList : ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_tag)

        supportActionBar?.hide()

        val search_key = intent.getStringExtra("tag_key")

        val url_s_tag = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"


        val jsonfile_s_tag = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Project")
            put("filter",JSONObject().apply {
                put("domains",JSONObject().apply {
                    put("$"+"in",JSONArray().apply {
                        put(0,search_key)
                    })
                })
            })
        }
        listview = findViewById(R.id.customListView_search)
        var tagAdapter : TagAdapter
        val volleyQueue = Volley.newRequestQueue(this)

        val request_s_tag: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url_s_tag, jsonfile_s_tag,
            Response.Listener<JSONObject> { response ->

                if (!response.get("document").equals(null)) {
                    for (i in 0 until response.getJSONArray("documents").length()) {
                        tagList.add(
                            response.getJSONArray("documents").getJSONObject(i).get("name")
                                .toString()
                        )
                    }

                    tagAdapter = TagAdapter(this, tagList, name_user)

                    listview.adapter = tagAdapter

                }
                else{
                    startActivity(Intent(this,RecommendationActivity::class.java))
                    Toast.makeText(this,"Tag not found",Toast.LENGTH_LONG).show()
                }
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
        request_s_tag.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        volleyQueue.add(request_s_tag)



    }
}