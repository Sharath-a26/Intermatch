package com.example.intermatch

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_upload_interest.*
import kotlinx.android.synthetic.main.activity_upload_project.*
import org.json.JSONArray
import org.json.JSONObject
var tag_inter = emptyArray<String>()
var isclicked_inter = BooleanArray(10000)
class UploadInterestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_interest)
        supportActionBar?.hide()

        val volleyQueue = Volley.newRequestQueue(this)

        val alertbuilder = AlertDialog.Builder(this)

        /**
         * getting user details from upload user details activity
         */
        val user_name = intent.getStringExtra("username")
        val user_email = intent.getStringExtra("user_email")
        val user_pass = intent.getStringExtra("user_pass")
        val user_dept = intent.getStringExtra("sel_dept")
        val user_type = intent.getStringExtra("usertype")
        val aboutme = intent.getStringExtra("aboutme")
        val github = intent.getStringExtra("github")
        val linkedin = intent.getStringExtra("linkedin")


        Log.d(null,"username = " + user_name)
        Log.d(null,"email = "+user_email)
        Log.d(null,"pass = "+user_pass)
        Log.d(null,"aboutme = "+aboutme)
        Log.d(null,"github = "+github)
        Log.d(null,"linkedin = "+linkedin)



        var checkedIndex = ArrayList<String>()

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
                tag_inter = temp_array.toTypedArray()
                isclicked_inter = temp_array2.toBooleanArray()
                Log.d(null,tag_inter[0])
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


        for (i in 0 until tag_inter.size) {
            isclicked_inter[i] = false
        }
        select_inter_btn.setOnClickListener {
            Log.d(null, isclicked_inter.toString())
            alertbuilder.setMultiChoiceItems(tag_inter, isclicked_inter, DialogInterface.OnMultiChoiceClickListener{
                    dialog,index,checked ->

                if (input_interests.isVisible == true) {
                    input_interests.text.clear()
                }
                if (checked) {
                    //input.text?.append("\n ${arr.get(index)}")
                    isclicked_inter[index] = checked
                    checkedIndex.add(tag_inter.get(index))

                }
                else if (checkedIndex.contains(tag_inter.get(index))) {
                    checkedIndex.remove(tag_inter.get(index))
                    isclicked_inter[index] = false
                }
            })
            alertbuilder.setPositiveButton("OK", DialogInterface.OnClickListener{
                    dialog,id ->
                for (i in 0 until checkedIndex.size) {
                    if (i != checkedIndex.size-1) {
                        input_interests?.append("${checkedIndex.get(i)}\n")
                    }
                    else {
                        input_interests?.append(checkedIndex.get(i))
                    }
                }

                input_interests.isVisible = true

            })
            alertbuilder.create().show()


            add_inter_btn.setOnClickListener {
                if (checkedIndex.size > 0) {

                    add_inter_btn.text = "NEXT"

                    val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

                    val a = JSONObject().apply {
                        put("dataSource","Cluster0")
                        put("database","Intermatch")
                        put("collection","User")
                        put("document",JSONObject().apply {
                            put("username",user_name)
                            put("email",user_email)
                            put("password",user_pass)
                            put("dept",user_dept)
                            put("Type",user_type)
                            put("aboutme",aboutme)
                            put("github",github)
                            put("linkedin",linkedin)
                            put("areas_of_interest",JSONArray().apply {
                                for (i in 0 until checkedIndex.size) {
                                    put(i,checkedIndex[i])
                                }
                            })
                        })
                        /*put("update",JSONObject().apply {
                            put("$"+"set",JSONObject().apply {
                                put("username",user_name)
                                put("areas_of_interest",JSONArray().apply {
                                    for (i in 0 until checkedIndex.size) {
                                        put(i,checkedIndex[i])
                                    }
                                })
                            })
                        })*/
                    }

                    val request: JsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url, a,
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
                            headers.put("Access-Control-Request-Headers","*");

                            return headers
                        }
                    }
                    volleyQueue.add(request)

                    add_inter_btn.setOnClickListener {

                        val intent = Intent(this@UploadInterestActivity,LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

                else {
                    Toast.makeText(this,"No Items selected",Toast.LENGTH_LONG).show()
                }
            }
        }

        /**
         * adding new domains
         */

        new_inter_btn.setOnClickListener {
            if (edit_new_domain.text.equals("")) {
                Toast.makeText(this,"Please enter a valid domain",Toast.LENGTH_LONG).show()
            }
            else {
                input_interests.append("\n" + edit_new_domain.text)

                val url_new_tag =
                    "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"
                val jsonfile_new_tag = JSONObject().apply {
                    put("dataSource", "Cluster0")
                    put("database", "Intermatch")
                    put("collection", "Tags")
                    put("document", JSONObject().apply {
                        put("tag", edit_new_domain.text)
                    })
                }

                val request_new_tag: JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url_new_tag, jsonfile_new_tag,
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
                        headers.put("Access-Control-Request-Headers","*");

                        return headers
                    }
                }
                volleyQueue.add(request_new_tag)


            }
        }

    }
}