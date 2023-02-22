package com.example.intermatch

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt


var domain_prj = JSONArray()
class SearchActivity : AppCompatActivity() {
    private var grid : GridView? = null
    private var arrayList : ArrayList<LanguageItem>? = null
    private var languageAdapter : LanguageAdapter? = null
    var search_like_count : Int = 0
    var prj_desc = ""
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
        val list_liked = intent.getStringArrayListExtra("listliked")
        lateinit var fac_email : String

        if (list_liked != null) {
            search_like_btn.setImageResource(R.drawable.heart_pressed)
            search_like_count = 1
        }

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

                if (response.get("document").equals(null)) {
                    startActivity(Intent(this,RecommendationActivity::class.java))
                    Toast.makeText(this,"Project not found",Toast.LENGTH_LONG).show()
                    no_data_found.isVisible = true
                }
                else {
                    search_prj_name.text = response.getJSONObject("document").get("name").toString()
                    search_dept_name.text =
                        response.getJSONObject("document").get("dept").toString()
                    search_researcher_name.text =
                        response.getJSONObject("document").get("faculty_name").toString()
                    fac_email = response.getJSONObject("document").get("faculty_email").toString()




                    prj_desc = response.getJSONObject("document").get("desc").toString()
                    domain_prj = response.getJSONObject("document").getJSONArray("domains")

                    var temp = ArrayList<String>()
                    var count1 = 0
                    for (j in 0 until domain_prj.length()) {
                        temp.add(domain_prj.getString(j))
                    }

                    if (user_inter != null) {
                        for (y in 0 until user_inter.size) {
                            if (user_inter.get(y) in temp) {
                                count1++
                            }
                        }
                    }

                    if (domain_prj.length() != 0) {


                        search_progressBar.progress =
                            ((count1.toFloat() / domain_prj.length()) * 100.0).roundToInt()

                        search_match_percentage.text =
                            ((count1.toFloat() / domain_prj.length()) * 100.0).roundToInt()
                                .toString() + "%"
                    }
                    else {
                        search_progressBar.progress = 0
                        search_match_percentage.text = "0%"
                    }

                    grid = findViewById<GridView>(R.id.search_grid_view)
                    arrayList = ArrayList()
                    arrayList = setDataList()
                    languageAdapter = LanguageAdapter(applicationContext, arrayList!!)
                    grid?.adapter = languageAdapter
                    // grid?.onItemClickListener = this

                    search_layout.isVisible = true
                }

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
            intent1.putExtra("faculty_email", fac_email)
            intent1.putExtra("project_name", search_prj_name.text)
            intent1.putExtra("faculty_name", search_researcher_name.text)
            intent1.putExtra("username", user_name)
            intent1.putExtra("match", search_match_percentage.text)
            intent1.putExtra("github", user_github)
            intent1.putExtra("linkedin", user_linkedin)
            startActivity(intent1)
        }

        /**
         * show researcher profile one touching it
         */
        search_researcher_name.setOnClickListener {
            startActivity(Intent(this,ShowProfileActivity::class.java).putExtra("shown_user",search_researcher_name.text.toString()))
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
                search_like_btn.setImageResource(R.drawable.heart__1_)
                if (search_like_count > 0) {
                    Log.d(null,"Deleting likes")


                    val url2 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/deleteMany"

                    val jsonfile2 = JSONObject().apply {
                        put("dataSource","Cluster0")
                        put("database","Intermatch")
                        put("collection","Liked_projects")
                        put("filter", JSONObject().apply {
                            put("name",search_prj_name.text)
                            put("faculty_name",search_researcher_name.text)
                            put("username",user_name)

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



        search_info_btn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
// ...Irrelevant code for customizing the buttons and title
            val dialogView = layoutInflater.inflate(R.layout.activity_info_alert, null)
            dialogBuilder.setView(dialogView)

            val editText =  dialogView.findViewById<TextView>(R.id.prj_desc)
            editText.setText(prj_desc)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
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