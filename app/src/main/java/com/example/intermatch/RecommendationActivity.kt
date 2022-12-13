package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_recommendation.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt
import kotlin.properties.Delegates
/**
    recommendation page
    1. User can navigate to liked projects and profile
    2. Like projects and send email to faculty
 */
var k : JSONArray = JSONArray()
var user_interest : JSONArray = JSONArray()
var i:Int = 0

var domains : JSONArray = JSONArray()

lateinit var temp : String
class RecommendationActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var gridView : GridView? = null
    private var arrayList : ArrayList<LanguageItem>? = null
    private var languageAdapter : LanguageAdapter? = null
    var x1 by Delegates.notNull<Float>()
    var x2 by Delegates.notNull<Float>()
    var y1 by Delegates.notNull<Float>()
    var y2 by Delegates.notNull<Float>()
    var like_count : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        supportActionBar?.hide()

        recommendation_layout.isVisible = false
        supportActionBar?.hide()

        /**
         * username obtained from login
         */
        var username = intent.getStringExtra("username")
        if (username != null) {
            temp = username
        }
        else
        {
            username = temp
        }
        val volleyQueue = Volley.newRequestQueue(this)

        /**
         * getting the user areas of interest through an api call to user collection
         */


        val url_user = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
        val jsonfile_user = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","User")
            put("filter", JSONObject().apply {
                put("username","sharathsr")
            })
        }

        val request_user_details : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url_user, jsonfile_user,
            Response.Listener<JSONObject> { response ->

                user_interest = response.getJSONObject("document").getJSONArray("areas_of_interest")
                Log.d(null,"user_interest = " + user_interest.toString())

            },
            Response.ErrorListener { error ->
                prj_name.text = error.toString()
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
        request_user_details.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        volleyQueue.add(request_user_details)

        /**
         * recommendation for the user
         */
        if (k.length() == 0) {


            val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"

            val jsonfile = JSONObject().apply {
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","Project")
                put("filter", JSONObject().apply {
                    put("dept","CSE")
                })
            }

            val request : JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url, jsonfile,
                Response.Listener<JSONObject> { response ->
                    k = response.getJSONArray("documents")


                    val prj = response.getJSONArray("documents").getJSONObject(intent.getIntExtra("text",i))
                        .get("name").toString()


                    val dept = response.getJSONArray("documents").getJSONObject(intent.getIntExtra("text",i))
                        .get("dept").toString()
                    val fac_name = response.getJSONArray("documents").getJSONObject(intent.getIntExtra("text",i))
                        .get("faculty_name").toString()
                    domains = response.getJSONArray("documents").getJSONObject(intent.getIntExtra("text",i))
                        .getJSONArray("domains")

                    prj_name.text = prj
                    dept_name.text = dept
                    researcher_name.text = fac_name

                    val domain_array = response.getJSONArray("documents").getJSONObject(intent.getIntExtra("text",i))
                        .getJSONArray("domains")

                    val temp : ArrayList<String> = ArrayList<String>()
                    var count1 = 0
                    for (i in 0 until domain_array.length()) {
                        temp.add(domain_array.getString(i))
                    }
                    for (i in 0 until user_interest.length()) {
                        if (user_interest.getString(i) in temp) {
                            count1++

                        }
                    }
                    Log.d(null,"count = " + count1.toString())

                    progressBar.progress = ((count1.toFloat()/domain_array.length())*100.0).roundToInt()
                    match_percentage.text = ((count1.toFloat()/domain_array.length())*100.0).roundToInt().toString() + "%"


                    gridView = findViewById(R.id.my_grid_view)
                    arrayList = ArrayList()
                    arrayList = setDataList()
                    languageAdapter = LanguageAdapter(applicationContext, arrayList!!)
                    gridView?.adapter = languageAdapter
                    gridView?.onItemClickListener = this
                    recommendation_layout.isVisible = true




                },
                Response.ErrorListener { error ->
                    prj_name.text = error.toString()
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

        /**
         * if the array of projects is already retrieved from database
         * No need to retrieve again, retrieve from a local array to minimise response time
         */
        else
        {
            val prj = k.getJSONObject(intent.getIntExtra("text",i))
                .get("name").toString()


            val dept = k.getJSONObject(intent.getIntExtra("text",i))
                .get("dept").toString()
            val fac_name = k.getJSONObject(intent.getIntExtra("text",i))
                .get("faculty_name").toString()
            domains = k.getJSONObject(intent.getIntExtra("text",i))
                .getJSONArray("domains")


            prj_name.text = prj
            dept_name.text = dept
            researcher_name.text = fac_name


            val domain_array = k.getJSONObject(intent.getIntExtra("text",i))
                .getJSONArray("domains")

            val temp : ArrayList<String> = ArrayList<String>()
            var count1 = 0
            for (i in 0 until domain_array.length()) {
                temp.add(domain_array.getString(i))
            }
            for (i in 0 until user_interest.length()) {
                if (user_interest.getString(i) in temp) {
                    count1++

                }
            }
            Log.d(null,count1.toString())
            progressBar.progress = ((count1.toFloat()/domain_array.length())*100.0).roundToInt()
            match_percentage.text = ((count1.toFloat()/domain_array.length())*100.0).roundToInt().toString()

            gridView = findViewById(R.id.my_grid_view)
            arrayList = ArrayList()
            arrayList = setDataList()
            languageAdapter = LanguageAdapter(applicationContext, arrayList!!)
            gridView?.adapter = languageAdapter
            gridView?.onItemClickListener = this
            recommendation_layout.isVisible = true
        }


        /**
         * changing the color of like button when pressed
         * if the like button is pressed then it is added to liked projects if it is not already present
         */

        like_btn.setOnClickListener {
            like_count++
            if (like_count % 2 == 1) {
                like_btn.setImageResource(R.drawable.heart_pressed)

                val url2 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

                val jsonfile2 = JSONObject().apply {
                    put("dataSource","Cluster0")
                    put("database","Intermatch")
                    put("collection","Liked_projects")
                    put("document", JSONObject().apply {
                        put("name",k.getJSONObject(intent.getIntExtra("text",i)).get("name").toString())
                        put("faculty_name",k.getJSONObject(intent.getIntExtra("text",i)).get("faculty_name").toString())
                        put("username",username)
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
            /**
             * if like button is pressed twice, remove the project from liked projects
             */
            else {

                like_btn.setImageResource(R.drawable.heart__1_)
                if (like_count > 0) {
                    val url2 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/delete"

                    val jsonfile2 = JSONObject().apply {
                        put("dataSource","Cluster0")
                        put("database","Intermatch")
                        put("collection","Liked_projects")
                        put("document", JSONObject().apply {
                            put("name",k.getJSONObject(intent.getIntExtra("text",i)).get("name").toString())
                            put("faculty_name",k.getJSONObject(intent.getIntExtra("text",i)).get("faculty_name").toString())
                            put("username",username)
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
         * navigating to different screens with bottom nav View
         */

        val main_view : BottomNavigationView = findViewById<BottomNavigationView>(R.id.navView)
        main_view.selectedItemId = R.id.item1
        main_view.setOnItemSelectedListener(
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
         * navigating to the search screen when the search item is entered
         */
        searchbtn.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val intent = Intent(this@RecommendationActivity,SearchActivity::class.java)
                    intent.putExtra("keyword",searchbtn.query.toString())
                    startActivity(intent)

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            }
        )


        /**
         * sending an email to faculty if outlook button pressed
         * 1. Takes user to send_email page
         */

    }

    /**
     * function to implement the grid view
     */
    private fun setDataList() : ArrayList<LanguageItem> {
        var arrayList:ArrayList<LanguageItem> = ArrayList()
        for (i in 0 until domains.length()) {
            arrayList.add(LanguageItem(domains.getString(i)))
        }

        return arrayList

    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event != null) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                x1 = event.x
                y1 = event.y
            }
            else if (event.action == MotionEvent.ACTION_UP) {
                x2 = event.x
                y2 = event.y
                if (x1 < x2) {
                    if (intent.getIntExtra("text",i) >0) {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", --i)

                        startActivity(intent)
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                    }
                    else {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", 0)

                        startActivity(intent)
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                    }
                }
                else if (x1 > x2) {
                    if (intent.getIntExtra("text",i) < k.length()-1) {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", ++i)

                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    else {
                        val intent = Intent(this, RecommendationActivity::class.java)
                        intent.putExtra("text", k.length()-1)

                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }

            }

        }

        return super.onTouchEvent(event)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var languageItem: LanguageItem = arrayList!!.get(position)
        Toast.makeText(applicationContext,languageItem.item, Toast.LENGTH_LONG).show()
    }

    }
