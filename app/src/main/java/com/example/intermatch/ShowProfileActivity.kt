package com.example.intermatch

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.profile_layout
import kotlinx.android.synthetic.main.activity_profile.profile_pass
import kotlinx.android.synthetic.main.activity_show_profile.*
import org.json.JSONArray
import org.json.JSONObject

class ShowProfileActivity : AppCompatActivity() {
    private var gridView : GridView? = null
    private var arrayList : ArrayList<LanguageItem>? = null
    private var languageAdapter : LanguageAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        supportActionBar?.hide()
        val show_user = intent.getStringExtra("shown_user")
        show_profile_layout.isVisible = false

        val url1 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
        val volleyQueue = Volley.newRequestQueue(this)
        val jsonfile_profile = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","User")
            put("filter", JSONObject().apply {
                put("username",show_user)
            })
        }
        gridView = findViewById(R.id.show_user_interests)

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url1, jsonfile_profile,
            Response.Listener<JSONObject> { response ->

                if (!(response.get("document").equals(null))) {

                    show_profile_user.text =
                        response.getJSONObject("document").get("username").toString()

                    show_profile_email.text =
                        response.getJSONObject("document").get("email").toString()

                    show_user_dept.text = response.getJSONObject("document").get("dept").toString()
                    show_aboutme.text = response.getJSONObject("document").get("aboutme").toString()
                    show_profile_github.text =
                        response.getJSONObject("document").get("github").toString()
                    show_profile_linkedin.text =
                        response.getJSONObject("document").get("linkedin").toString()
                    val areas_of_inter =
                        response.getJSONObject("document").getJSONArray("areas_of_interest")

                    arrayList = ArrayList()
                    arrayList = setDataList(areas_of_inter)

                    languageAdapter = LanguageAdapter(applicationContext, arrayList!!)
                    gridView?.adapter = languageAdapter
                   /* for (i in 0 until areas_of_inter.length()) {
                        if (i != areas_of_inter.length() - 1) {
                            show_user_interests?.append("${areas_of_inter.get(i).toString()}\n\n")
                        } else {
                            show_user_interests?.append(areas_of_inter.get(i).toString())
                        }
                    }*/
                    show_profile_layout.isVisible = true

                }
                else {

                    startActivity(Intent(this,RecommendationActivity::class.java))
                    Toast.makeText(this,"Profile not found",Toast.LENGTH_LONG).show()
                }

            },
            Response.ErrorListener { error ->
                profile_user.text = "error"
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

        /**
         * opening github accnt
         */

        show_profile_github.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(show_profile_github.text.toString())))
        }

        /**
         * opening linkedin accnt
         */

        show_profile_linkedin.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(show_profile_linkedin.text.toString())))
        }

        /**
         * goto recom page when back btn pressed
         */

        pro_back_btn.setOnClickListener {
            startActivity(Intent(this,RecommendationActivity::class.java))
        }
    }
    private fun setDataList(areas_of_inter: JSONArray) : ArrayList<LanguageItem> {
        var arrayList:ArrayList<LanguageItem> = ArrayList()
        for (i in 0 until areas_of_inter.length()) {
            arrayList.add(LanguageItem(areas_of_inter.getString(i)))
        }

        return arrayList

    }
}