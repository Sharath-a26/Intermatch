package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_custom_list_view.view.*
import kotlinx.android.synthetic.main.activity_liked.*
import kotlinx.android.synthetic.main.activity_student_request.*
import org.json.JSONObject
var req_projects : ArrayList<String> = ArrayList()
class StudentRequestActivity : AppCompatActivity() {
    var prjList : ArrayList<String> = ArrayList()
    lateinit var listview : ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_request)
        supportActionBar?.hide()
        val username = intent.getStringExtra("username")
        val user_type = intent.getStringExtra("usertype")
        val position = intent.getIntExtra("position",-1)
        val requested_view = findViewById<BottomNavigationView>(R.id.navView)

        /**
         * navigation to different screens
         */
        menuInflater.inflate(R.menu.bottom_menu_faculty,requested_view.menu)
        listview = findViewById(R.id.customListView)


        var intent1 = Intent()

        requested_view.selectedItemId = R.id.item_fac_4

        requested_view.setOnItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.item_fac_1 -> {
                        intent1 = Intent(this, RecommendationActivity::class.java)
                        intent1.putExtra("username", username)
                        intent1.putExtra("usertype",user_type)
                        startActivity(intent1)
                    }
                    R.id.item_fac_2 -> {
                        intent1 = Intent(this, LikedActivity::class.java)
                        intent1.putExtra("username", username)
                        intent1.putExtra("usertype",user_type)
                        startActivity(intent1)
                    }

                    R.id.item_fac_3 -> {
                        intent1 = Intent(this,AddProjectActivity::class.java)
                        intent1.putExtra("username",username)
                        startActivity(intent1)
                    }

                    R.id.item_fac_4 -> {
                        intent1 = Intent(this,StudentRequestActivity::class.java)
                        intent1.putExtra("username",username)
                        intent1.putExtra("usertype",user_type)
                        startActivity(intent1)
                    }
                    R.id.item_fac_5 -> {
                        intent1 = Intent(this, ProfileActivity::class.java)
                        intent1.putExtra("username", username)
                        intent1.putExtra("usertype",user_type)
                        startActivity(intent1)
                    }
                }
                true
            }
        )


        /**
         * when a user presses a particular row
         */



        var customBaseAdapter : CustomBaseAdapter
        req_layout.isVisible = false
        /**
         * List view for displaying the liked projects
         */
        val volleyQueue = Volley.newRequestQueue(this)
        val url_user_prjs = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"

        val jsonfile_user_prjs = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Project")
            put("filter", JSONObject().apply {
                put("faculty_name",intent.getStringExtra("username"))
            })
        }

        val request_user_prjs : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url_user_prjs, jsonfile_user_prjs,
            Response.Listener<JSONObject> { response ->

                for (i in 0 until response.getJSONArray("documents").length()) {
                    req_projects.add(response.getJSONArray("documents").getJSONObject(i).get("name").toString())
                }
                for (i in 0 until req_projects.size) {
                    if (!(req_projects.get(i) in prjList)) {
                        prjList.add(req_projects.get(i))
                    }
                }

                Log.d(null,prjList.toString())

                /*var arrayAdapter = ArrayAdapter(applicationContext,R.layout.activity_custom_list_view,R.id.list_text,prjList)
                Log.d(null,"sdbdfbg")
                listview.adapter = arrayAdapter

                listview.setOnItemClickListener { parent, view, position, id ->


                        val intent2 = Intent(this,AboutProjectActivity::class.java)
                        intent2.putExtra("prjname", req_projects.get(position))
                        intent2.putExtra("faculty_name",username)
                        startActivity(intent2)

                }*/
                if (position != -1) {
                    prjList.removeAt(position)
                }
                var prjAdapter = username?.let { ProjectAdapter(applicationContext,prjList, it) }
                listview.adapter = prjAdapter
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
        request_user_prjs.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        volleyQueue.add(request_user_prjs)

        req_layout.isVisible = true



    }
}

