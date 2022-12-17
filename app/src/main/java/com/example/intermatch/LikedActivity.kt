package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_liked.*
import org.json.JSONObject

var liked_projects : ArrayList<String> = ArrayList()
class LikedActivity : AppCompatActivity() {

    var likeList : ArrayList<String> = ArrayList()
    lateinit var listview : ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked)
        supportActionBar?.hide()

        /**
         * Bottom Navigation with from the liked page
         */
        val username = intent.getStringExtra("username")
        val like_view : BottomNavigationView = findViewById<BottomNavigationView>(R.id.navView)
        like_view.selectedItemId = R.id.item2
        like_view.setOnItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.item1 -> startActivity(Intent(this,RecommendationActivity::class.java)
                        .putExtra("username",username))
                    R.id.item2 -> startActivity(Intent(this,LikedActivity::class.java)
                        .putExtra("username",username))

                    R.id.item3 -> startActivity(Intent(this,ProfileActivity::class.java)
                        .putExtra("username",username))
                }
                true
            }
        )


        /**
         * creating a listview to display the list of liked projects
         */


        listview = findViewById(R.id.customListView)
        var customBaseAdapter : CustomBaseAdapter
        like_layout.isVisible = false
        /**
         * List view for displaying the liked projects
         */
        val volleyQueue = Volley.newRequestQueue(this)
        val url_liked = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"

        val jsonfile_liked = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Liked_projects")
            put("filter", JSONObject().apply {
                    put("username",intent.getStringExtra("username"))
            })
        }

        val request_liked : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url_liked, jsonfile_liked,
            Response.Listener<JSONObject> { response ->

                for (i in 0 until response.getJSONArray("documents").length()) {
                    liked_projects.add(response.getJSONArray("documents").getJSONObject(i).get("name").toString())
                }
                for (i in 0 until liked_projects.size) {
                    if (!(liked_projects.get(i) in likeList)) {
                        likeList.add(liked_projects.get(i))
                    }
                }

                Log.d(null,likeList.toString())

                customBaseAdapter =  CustomBaseAdapter(this.baseContext, likeList)
                Log.d(null,"sdbdfbg")
                listview.adapter = customBaseAdapter

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
        request_liked.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        volleyQueue.add(request_liked)

        like_layout.isVisible = true
        //var customBaseAdapter: CustomBaseAdapter =


    }
}


