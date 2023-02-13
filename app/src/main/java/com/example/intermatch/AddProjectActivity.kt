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
import kotlinx.android.synthetic.main.activity_add_project.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_upload_project.*
import kotlinx.android.synthetic.main.activity_upload_project.input
import org.json.JSONArray
import org.json.JSONObject
var fac_email : String = ""
var tags = emptyArray<String>()
var isclicked = BooleanArray(10000)
class AddProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        supportActionBar?.hide()
        val faculty_name = intent.getStringExtra("username")
        val alertbuilder = AlertDialog.Builder(this)
        var checkedIndex = ArrayList<String>()


        val volleyQueue = Volley.newRequestQueue(this)
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
                Log.d(null,"chumma")
                val temp_array = ArrayList<String>()
                val temp_array2 = ArrayList<Boolean>()
                for (i in 0 until response.getJSONArray("documents").length()) {
                    temp_array.add(response.getJSONArray("documents").getJSONObject(i).get("tag").toString())
                    temp_array2.add(false)
                }
                tags = temp_array.toTypedArray()
                isclicked = temp_array2.toBooleanArray()

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

        /**
         * adding department for the project
         */
        var departments : Array<CharSequence> = arrayOf("AEE","AIE","ARE","CCE","CHE","CIE","CVI","CSE","CYS","EAC","ECE","EEE","EIE","ELC","MEE")
        var alertbuilder2 = AlertDialog.Builder(this)
        prj_dept_add.setOnClickListener {
            lateinit var dept_selected : String
            alertbuilder2.setTitle("Select Project Department")
            alertbuilder2.setSingleChoiceItems(departments,0,DialogInterface.OnClickListener { dialog, which ->
                dept_selected = departments[which] as String
            })

            alertbuilder2.setPositiveButton("OK",DialogInterface.OnClickListener { dialog, id ->
                prj_dept_add.text = dept_selected
            })
            alertbuilder2.create().show()
        }


        Log.d(null, "Tag size = " + tags.size)
        Log.d(null,"Tags = " + tags.toString())



            upload_domain_btn_add.setOnClickListener {
                if (tags.size != 0) {
                    Log.d(null,tags[1])
                Log.d(null, "upload_btn clicked")
                alertbuilder.setTitle("Select an option")
                alertbuilder.setMultiChoiceItems(
                    tags,
                    isclicked,
                    DialogInterface.OnMultiChoiceClickListener { dialog, index, checked ->
                        if (checked) {

                            //input.text?.append("\n ${arr.get(index)}")
                            checkedIndex.add(tags.get(index))
                            isclicked[index] = checked
                        } else if (checkedIndex.contains(tags.get(index))) {

                            checkedIndex.remove(tags.get(index))
                            isclicked[index] = false
                        }
                    })
                alertbuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    for (i in 0 until checkedIndex.size) {
                        if (i != checkedIndex.size - 1) {
                            if (!(checkedIndex.get(i) in input_add.text)) {
                                input_add.text?.append("${checkedIndex.get(i)} \n")
                            }
                        } else {
                            input_add.text?.append(checkedIndex.get(i))
                        }
                    }
                    input_add.isVisible = true

                })
                alertbuilder.create().show()


            }
                else{
                    Log.d(null,"tag size 0 ")
                }
        }

        if (new_domain_add.text != null) {

            input_add.append(new_domain_add.text)

            val url_append_tag = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"
            val append_tag = JSONObject().apply {
                put("dataSource", "Cluster0")
                put("database", "Intermatch")
                put("collection", "Tags")
                put("document", JSONObject().apply {
                    put("tag", new_domain_add.text)
                })
            }

            val request_append_tag: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url_append_tag, append_tag,
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
            volleyQueue.add(request_append_tag)

        }

        addprj_add.setOnClickListener {

            if (prjname_add.text.toString().equals("")) {
                Toast.makeText(this,"Please provide a project name",Toast.LENGTH_LONG).show()
            }

            else {

                val description = edit_desc_add.text


                val url1 =
                    "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/findOne"
                val jsonfile1 = JSONObject().apply {
                    put("dataSource", "Cluster0")
                    put("database", "Intermatch")
                    put("collection", "User")
                    put("filter", JSONObject().apply {
                        put("username", username)
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
                                    put(checkedIndex.size, new_domain_add.text)
                                }

                                )
                                put("dept", prj_dept_add.text)
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
                                Toast.makeText(this, error.message.toString(), Toast.LENGTH_LONG)
                                    .show();

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




                Log.d(null, "faculty_email = " + fac_email)
                /**
                 * navigating to the recommendation screen after the project is uploaded
                 */

                val intent1 = Intent(this@AddProjectActivity, RecommendationActivity::class.java)
                intent1.putExtra("username", faculty_name)
                intent1.putExtra("usertype", "Faculty")
                startActivity(intent1)
            }
        }

        /**
         * adding new tags
         */

        new_prj_domain_add.setOnClickListener {
            input_add.isVisible = true
            if (checkedIndex.contains(new_domain_add.text.toString())) {
                Toast.makeText(this, "Domain added to list", Toast.LENGTH_LONG).show()
            } else {
                checkedIndex.add(new_domain_add.text.toString())

                if (!input_add.text.equals("")) {
                    input_add.append("\n" + new_domain_add.text)
                }
                else{
                    input_add.append(new_domain_add.text.toString() + "\n")
                }

                val url_append_tag =
                    "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"
                val append_tag = JSONObject().apply {
                    put("dataSource", "Cluster0")
                    put("database", "Intermatch")
                    put("collection", "Tags")
                    put("document", JSONObject().apply {
                        put("tag", new_domain_add.text)
                    })
                }

                val request_append_tag: JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url_append_tag, append_tag,
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
                volleyQueue.add(request_append_tag)
            }
        }


    }
}

