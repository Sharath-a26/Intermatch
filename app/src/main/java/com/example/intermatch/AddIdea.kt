package com.example.intermatch

import android.app.AlertDialog
import android.content.DialogInterface
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
import kotlinx.android.synthetic.main.activity_add_idea.*
import kotlinx.android.synthetic.main.activity_add_project.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
var stud_email : String = ""
var tag_idea = emptyArray<String>()
var isclicked_idea = BooleanArray(10000)
class AddIdea : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_idea)
        supportActionBar?.hide()
        val username = intent.getStringExtra("username")
        val volleyQueue = Volley.newRequestQueue(this)
        val alertbuilder = AlertDialog.Builder(this)
        var checkedIndex = ArrayList<String>()


        /**
         * get tags from database
         */

        val url_tags = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/find"
        val jsonfile_tags = JSONObject().apply {
            put("dataSource","Cluster0")
            put("database","Intermatch")
            put("collection","Tags")
            put("filter",JSONObject().apply {

            })
        }

        val request_tag: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url_tags, jsonfile_tags,
            Response.Listener<JSONObject> {
                    response ->

                val temp_array = ArrayList<String>()
                val temp_array2 = ArrayList<Boolean>()
                for (i in 0 until response.getJSONArray("documents").length()) {
                    temp_array.add(response.getJSONArray("documents").getJSONObject(i).get("tag").toString())
                    temp_array2.add(false)
                }
                tag_idea = temp_array.toTypedArray()
                isclicked_idea = temp_array2.toBooleanArray()
                Log.d(null,tag_idea[0])
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show();

            }
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
        request_tag.setRetryPolicy(
            DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )



        volleyQueue.add(request_tag);






            upload_idea_domain.setOnClickListener {
                if (tag_idea.size != 0) {
                Log.d(null, "upload_btn clicked")
                alertbuilder.setTitle("Select an option")
                alertbuilder.setMultiChoiceItems(
                    tag_idea,
                    isclicked_idea,
                    DialogInterface.OnMultiChoiceClickListener { dialog, index, checked ->
                        if (checked) {

                            //input.text?.append("\n ${arr.get(index)}")
                            checkedIndex.add(tag_idea.get(index))
                            isclicked_idea[index] = checked
                        } else if (checkedIndex.contains(tag_idea.get(index))) {

                            checkedIndex.remove(tag_idea.get(index))
                            isclicked_idea[index] = false
                        }
                    })
                alertbuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->


                })
                alertbuilder.create().show()
            }
        }

        add_idea_btn.setOnClickListener {

            Log.d(null,"adbfdfndng")

            val description = idea_desc.text


            val url1 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
            val jsonfile1 = JSONObject().apply {
                put("dataSource", "Cluster0")
                put("database", "Intermatch")
                put("collection", "User")
                put("filter", JSONObject().apply {
                    put("username",username)
                })

            }

            val request1: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url1, jsonfile1,
                Response.Listener<JSONObject> { response ->
                    stud_email = response.getJSONObject("document").get("email").toString()

                    val url2 =
                        "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

                    val idea_name = idea_name.text.toString()

                    Log.d(null, "faculty_name = " + stud_email)

                    val a = JSONObject().apply {
                        put("dataSource", "Cluster0")
                        put("database", "Intermatch")
                        put("collection", "Project")
                        put("document", JSONObject().apply {
                            put("faculty_name", username)
                            put("name", idea_name)
                            put("faculty_email", stud_email)
                            put("domains", JSONArray().apply {
                                for (i in 0..checkedIndex.size - 1) {
                                    put(i, checkedIndex[i])
                                }
                            }

                            )
                            put("desc", description)
                        })

                    }
                    Log.d(null, "helo")
                    //val jsonfile = JSONObject(info)

                    val request2: JsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url2, a,
                        Response.Listener<JSONObject> { response ->
                            Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(this, error.message.toString(), Toast.LENGTH_LONG).show();

                        }) {


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

                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.message.toString(), Toast.LENGTH_LONG).show();

                }) {


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
            volleyQueue.add(request1)

        }

    }
}