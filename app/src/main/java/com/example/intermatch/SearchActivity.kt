package com.example.intermatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()

        /**
         * implementing search
         */
        val keyword = intent.getStringExtra("keyword")
        search_layout.isVisible = false
        val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
        val jsonfile = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Project")
            put("filter", JSONObject().apply {
                put("name",keyword)
            })
        }

        if (keyword != null) {
            Log.d(null,keyword)
        }

        val volleyQueue = Volley.newRequestQueue(this)
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url, jsonfile,
            Response.Listener<JSONObject> { response ->

                search_prj_name.text = response.getJSONObject("document").get("name").toString()
                search_dept_name.text = response.getJSONObject("document").get("dept").toString()
                search_researcher_name.text = response.getJSONObject("document").get("faculty_name").toString()
                search_domain.text = response.getJSONObject("document").getJSONArray("domains").toString()

                search_layout.isVisible = true

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Project not found", Toast.LENGTH_LONG).show()
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