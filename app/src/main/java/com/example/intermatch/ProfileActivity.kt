package com.example.intermatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_login.username
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()
        profile_layout.isVisible = false
        val username = intent.getStringExtra("username")
        val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
        val volleyQueue = Volley.newRequestQueue(this)
        val jsonfile = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","User")
            put("filter",JSONObject().apply {
                put("username",intent.getStringExtra("username"))
            })
        }

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url, jsonfile,
            Response.Listener<JSONObject> { response ->

                       profile_user.text = response.getJSONObject("document").get("username").toString()

                        profile_email.text = response.getJSONObject("document").get("email").toString()
                        profile_pass.text = response.getJSONObject("document").get("password").toString()
                        user_dept.text = response.getJSONObject("document").get("dept").toString()
                        val areas_of_inter = response.getJSONObject("document").getJSONArray("areas_of_interest")
                        for (i in 0 until areas_of_inter.length()) {
                            if (i != areas_of_inter.length()-1) {
                                user_interests?.append("${areas_of_inter.get(i).toString()}\n\n")
                            }
                            else {
                                user_interests?.append(areas_of_inter.get(i).toString())
                            }
                        }
                        profile_layout.isVisible = true



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

        //profile_layout.isVisible = true
        /**
         * updating the profile when user clicks the update button
         */
        update_btn.setOnClickListener {


            if (update_btn.text == "update profile") {
                Toast.makeText(this, "Update Profile", Toast.LENGTH_LONG).show()
                update_btn.text = "save profile"
                profile_user.isVisible = false
                profile_email.isVisible = false
                profile_pass.isVisible = false

                edit_username.setText("${profile_user.text}")
                edit_email.setText("${profile_email.text}")
                edit_pass.setText("${profile_pass.text}")

                edit_username.isVisible = true
                edit_email.isVisible = true
                edit_pass.isVisible = true
            }
            /**
             * after editing, if the user clicks save profile
             */
            else {
                edit_username.isVisible = false
                edit_email.isVisible = false
                edit_pass.isVisible = false

                profile_user.text = edit_username.text
                profile_email.text = edit_email.text
                profile_pass.text = edit_pass.text

                profile_user.isVisible = true
                profile_email.isVisible = true
                profile_pass.isVisible = true


                update_btn.text = "update profile"

                val url_save = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/updateOne"
                val jsonfile_save = JSONObject().apply {
                    put("dataSource","Cluster0")
                    put("database","Intermatch")
                    put("collection","User")
                    put("filter",JSONObject().apply {
                        put("username",username)
                    })
                    put("update",JSONObject().apply {
                        put("$"+"set", JSONObject().apply {
                            put("username",profile_user.text)
                            put("email",profile_email.text)
                            put("password",profile_pass.text)
                        })
                    })

                }


                val request_save : JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url_save, jsonfile_save,
                    Response.Listener<JSONObject> { response ->
                        Toast.makeText(this@ProfileActivity,"success",Toast.LENGTH_LONG).show()
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this@ProfileActivity,error.message,Toast.LENGTH_LONG).show()
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



                volleyQueue.add(request_save)

            }


            /*
            update_btn.text = "Save Profile"
            profile_user.isVisible = false
            profile_email.isVisible = false
            profile_pass.isVisible = false

            edit_username.text = profile_user.text as Editable?
            edit_email.text = profile_email.text as Editable?
            edit_pass.text = profile_pass.text as Editable?

            edit_username.isVisible = true
            edit_email.isVisible = true
            edit_pass.isVisible = true

            /**
             * after editing, if the user clicks save profile
             */
            update_btn.setOnClickListener {
                edit_username.isVisible = false
                edit_email.isVisible = false
                edit_pass.isVisible = false

                profile_user.text = edit_username.text
                profile_email.text = edit_email.text
                profile_pass.text = edit_pass.text

                profile_user.isVisible = true
                profile_email.isVisible = true
                profile_pass.isVisible = true


                update_btn.text = "Update profile"


            }

             */


        }


        /**
        * Bottom nav view
         */
        val profile_view : BottomNavigationView = findViewById<BottomNavigationView>(R.id.navView)
        profile_view.selectedItemId = R.id.item3
        profile_view.setOnItemSelectedListener(
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
    }
}
