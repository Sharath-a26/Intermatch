package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_recommendation.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.search_match_percentage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


var domain_prj = JSONArray()
class SearchActivity : AppCompatActivity() {
    private var grid : GridView? = null
    private var arrayList : ArrayList<LanguageItem>? = null
    private var languageAdapter : LanguageAdapter? = null
    var search_like_count : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()

        /**
         * implementing search
         */
        val keyword = intent.getStringExtra("keyword")
        var user_inter = intent.getCharSequenceArrayListExtra("user_interest")
        var github_user = intent.getStringExtra("github")
        var linkedin_user = intent.getStringExtra("linkedin")
        val user_name = intent.getStringExtra("username")
        search_layout.isVisible = false
        val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"


        val jsonfile = JSONObject().apply {
            put("dataSource", "Cluster0")
            put("database", "Intermatch")
            put("collection", "Project")
            put("filter", JSONObject().apply {
                put("name", JSONObject().apply {
                    put("$" + "regex", keyword)
                    put("$" + "options", "$" + "i")
                })
            })
        }

        if (keyword != null) {
            Log.d(null, keyword)
        }

        val volleyQueue = Volley.newRequestQueue(this)
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url, jsonfile,
            Response.Listener<JSONObject> { response ->

                search_prj_name.text = response.getJSONObject("document").get("name").toString()
                search_dept_name.text = response.getJSONObject("document").get("dept").toString()
                search_researcher_name.text =
                    response.getJSONObject("document").get("faculty_name").toString()
                domain_prj = response.getJSONObject("document").getJSONArray("domains")


                grid = findViewById<GridView>(R.id.search_grid_view)
                arrayList = ArrayList()
                arrayList = setDataList()
                languageAdapter = LanguageAdapter(applicationContext, arrayList!!)
                grid?.adapter = languageAdapter
                // grid?.onItemClickListener = this

                search_layout.isVisible = true

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Project not found", Toast.LENGTH_LONG).show()
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
        request.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        volleyQueue.add(request)

        /**
         * outlook btn in search activity
         */

        search_outlook_btn.setOnClickListener {
            val intent1 = Intent(this, SendEmailActivity::class.java)
            intent1.putExtra("faculty_email", faculty_email)
            intent1.putExtra("project_name", search_prj_name.text)
            intent1.putExtra("faculty_name", search_researcher_name.text)
            intent1.putExtra("username", user_name)
            intent1.putExtra("match", search_match_percentage.text)
            intent1.putExtra("github", user_github)
            intent1.putExtra("linkedin", user_linkedin)
            startActivity(intent1)
        }

        search_like_btn.setOnClickListener {

            search_like_count++
            if (search_like_count % 2 == 1) {
                search_like_btn.setImageResource(R.drawable.heart_pressed)

                val url2 =
                    "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

                val jsonfile2 = JSONObject().apply {
                    put("dataSource", "Cluster0")
                    put("database", "Intermatch")
                    put("collection", "Liked_projects")
                    put("document", JSONObject().apply {
                        put("name", search_prj_name.text)
                        put("faculty_name", search_researcher_name.text)
                        put("username", user_name)
                    })
                }


                val request2: JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url2, jsonfile2,
                    Response.Listener<JSONObject> { response2 ->

                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                    },
                    Response.ErrorListener { error2 ->
                        Toast.makeText(this, error2.message, Toast.LENGTH_LONG).show()
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

                volleyQueue.add(request2)

            }

            else {
                like_btn.setImageResource(R.drawable.heart__1_)
                if (search_like_count > 0) {
                    val url2 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/deleteMany"

                    val jsonfile2 = JSONObject().apply {
                        put("dataSource","Cluster0")
                        put("database","Intermatch")
                        put("collection","Liked_projects")
                        put("filter", JSONObject().apply {
                            put("name",prj_name.text)
                            put("faculty_name",researcher_name.text)

                        })
                    }


                    val request2 : JsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url2, jsonfile2,
                        Response.Listener<JSONObject> { response2 ->

                            Toast.makeText(this,"Success", Toast.LENGTH_LONG).show()
                        },
                        Response.ErrorListener { error2 ->
                            Toast.makeText(this,error2.message, Toast.LENGTH_LONG).show()
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
                    request2.setRetryPolicy(
                        DefaultRetryPolicy(
                            MY_SOCKET_TIMEOUT_MS,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                    )

                    volleyQueue.add(request2)
                }
            }

        }

        /**
         * goto recommendation when back button pressed
         */

        search_back.setOnClickListener {
            startActivity(Intent(this,RecommendationActivity::class.java))
        }

    }
    private fun setDataList(): ArrayList<LanguageItem> {
        var arrayList: ArrayList<LanguageItem> = ArrayList()
        for (i in 0 until domain_prj.length()) {
            arrayList.add(LanguageItem(domain_prj.getString(i)))
        }

        return arrayList

    }


}