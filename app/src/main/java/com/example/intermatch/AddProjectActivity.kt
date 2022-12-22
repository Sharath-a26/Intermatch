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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_add_project.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_upload_project.*
import kotlinx.android.synthetic.main.activity_upload_project.input
import org.json.JSONArray
import org.json.JSONObject
var fac_email : String = ""

class AddProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        supportActionBar?.hide()
        val faculty_name = intent.getStringExtra("username")
        val alertbuilder = AlertDialog.Builder(this)
        var checkedIndex = ArrayList<String>()
        val arr =resources.getStringArray(R.array.data_list)
        var isclicked = BooleanArray(arr.size)
        for (i in 0 until arr.size) {
            isclicked[i] = false
        }
        upload_domain_btn_add.setOnClickListener {
            Log.d(null,"upload_btn clicked")
            alertbuilder.setTitle("Select an option")
            alertbuilder.setMultiChoiceItems(R.array.data_list,isclicked, DialogInterface.OnMultiChoiceClickListener{
                    dialog,index,checked ->
                if (checked) {

                    //input.text?.append("\n ${arr.get(index)}")
                    checkedIndex.add(arr.get(index))
                    isclicked[index] = checked
                }
                else if (checkedIndex.contains(arr.get(index))) {

                    checkedIndex.remove(arr.get(index))
                    isclicked[index] = false
                }
            })
            alertbuilder.setPositiveButton("OK", DialogInterface.OnClickListener{
                    dialog,id ->
                for (i in 0 until checkedIndex.size) {
                    if (i != checkedIndex.size-1) {
                        input_add.text?.append("${checkedIndex.get(i)} \n")
                    }
                    else {
                        input_add.text?.append(checkedIndex.get(i))
                    }
                }
                input_add.isVisible = true

            })
            alertbuilder.create().show()
        }

        addprj_add.setOnClickListener {

            Log.d(null,"adbfdfndng")
            val volleyQueue = Volley.newRequestQueue(this)
            val description = edit_desc_add.text


            val url1 = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
            val jsonfile1 = JSONObject().apply {
                put("dataSource", "Cluster0")
                put("database", "Intermatch")
                put("collection", "User")
                put("filter",JSONObject().apply {
                    put("username",username)
                })

            }

            val request1: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url1, jsonfile1,
                Response.Listener<JSONObject> { response ->
                    fac_email = response.getJSONObject("document").get("email").toString()

                    val url2 =
                        "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

                    val project_name = prjname_add.text.toString()

                    Log.d(null, "faculty_name = " + fac_email)

                    val a = JSONObject().apply {
                        put("dataSource", "Cluster0")
                        put("database", "Intermatch")
                        put("collection", "Project")
                        put("document", JSONObject().apply {
                            put("faculty_name", faculty_name)
                            put("name", project_name)
                            put("faculty_email", fac_email)
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


            /**
             * navigating to the recommendation screen after the project is uploaded
             */

            val intent1 = Intent(this@AddProjectActivity,RecommendationActivity::class.java)
            intent1.putExtra("username",faculty_name)
            intent1.putExtra("usertype","Faculty")
            startActivity(intent1)

        }



    }
}