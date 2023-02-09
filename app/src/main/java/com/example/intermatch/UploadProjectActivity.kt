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
import kotlinx.android.synthetic.main.activity_upload_project.*
import org.json.JSONArray
import org.json.JSONObject

class UploadProjectActivity : AppCompatActivity() {
    var tags = emptyArray<String>()
    var isclicked = BooleanArray(10000)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_project)
        supportActionBar?.hide()

        /**
         * receiving user details
         */
        val faculty_email = intent.getStringExtra("faculty_email") //getting fac_email from register page
        val faculty_name = intent.getStringExtra("faculty_name") // getting fac_name from register page
        val user_pass = intent.getStringExtra("user_pass")
        val user_dept = intent.getStringExtra("sel_dept")
        val user_type = intent.getStringExtra("usertype")
        val aboutme = intent.getStringExtra("aboutme")
        val github = intent.getStringExtra("github")
        val linkedin = intent.getStringExtra("linkedin")


        val alertbuilder = AlertDialog.Builder(this)
        var checkedIndex = ArrayList<String>()


        val volleyQueue = Volley.newRequestQueue(this)
        /**
         * get the tags from database
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
                tags = temp_array.toTypedArray()
                isclicked = temp_array2.toBooleanArray()
                Log.d(null,tags[0])
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
         * selecting dept for the project
         */
        var departments : Array<CharSequence> = arrayOf("AEE","AIE","ARE","CCE","CHE","CIE","CVI","CSE","CYS","EAC","ECE","EEE","EIE","ELC","MEE")
        var alertbuilder2 = AlertDialog.Builder(this)
        prj_dept.setOnClickListener {
            lateinit var dept_selected : String
            alertbuilder2.setTitle("Select Project Department")
            alertbuilder2.setSingleChoiceItems(departments,0,DialogInterface.OnClickListener { dialog, which ->
                dept_selected = departments[which] as String
            })

            alertbuilder2.setPositiveButton("OK",DialogInterface.OnClickListener { dialog, id ->
                prj_dept.text = dept_selected
            })
            alertbuilder2.create().show()
        }

        upload_domain_btn.setOnClickListener {

            alertbuilder.setTitle("Select an option")
            alertbuilder.setMultiChoiceItems(tags,isclicked, DialogInterface.OnMultiChoiceClickListener{
                    dialog,index,checked ->
                if (checked) {

                    //input.text?.append("\n ${arr.get(index)}")
                    checkedIndex.add(tags.get(index))
                    isclicked[index] = checked
                }
                else if (checkedIndex.contains(tags.get(index))) {

                    checkedIndex.remove(tags.get(index))
                    isclicked[index] = false
                }
            })
            alertbuilder.setPositiveButton("OK", DialogInterface.OnClickListener{
                    dialog,id ->
                for (i in 0 until checkedIndex.size) {
                    if (i != checkedIndex.size-1) {
                        input.text?.append("${checkedIndex.get(i)} \n")
                    }
                    else {
                        input.text?.append(checkedIndex.get(i))
                    }
                }
                input.isVisible = true

            })
            alertbuilder.create().show()
        }



        addprj.setOnClickListener {

            if (prjname.text.toString().equals("")) {
                Toast.makeText(this,"Please provide a name to your project",Toast.LENGTH_LONG).show()
            }

            else {
                addprj.text = "NEXT"

                val description = edit_desc.text
                val url =
                    "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"
                val project_name = prjname.text.toString()

                val info = """  
                {
                "dataSource" : "Cluster0",
                 "database":"Intermatch",
                 "collection":"Project",
                 "document" : {
                    "faculty_name" : "shar",
                    "faculty_email" : "$faculty_email",
                    "name" : "$project_name",
                    "domains" : 
                 }
                }
            """.trimIndent()
                val a = JSONObject().apply {
                    put("dataSource", "Cluster0")
                    put("database", "Intermatch")
                    put("collection", "Project")

                    put("document", JSONObject().apply {

                        put("faculty_name", faculty_name)
                        put("name", project_name)
                        put("faculty_email", faculty_email)
                        put("domains", JSONArray().apply {
                            for (i in 0..checkedIndex.size - 1) {
                                put(i, checkedIndex[i])
                            }
                            put(checkedIndex.size, new_domain.text)
                        }

                        )
                        put("dept", prj_dept.text)
                        put("desc", description)
                    })


                }
                Log.d(null, "helo")
                //val jsonfile = JSONObject(info)

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
                        headers.put("Access-Control-Request-Headers", "*");

                        return headers
                    }
                }
                volleyQueue.add(request)




                addprj.setOnClickListener {
                    val intent =
                        Intent(this@UploadProjectActivity, UploadInterestActivity::class.java)
                    intent.putExtra("username", faculty_name)
                    intent.putExtra("user_email",faculty_email)
                    intent.putExtra("user_pass",user_pass)
                    intent.putExtra("sel_dept",user_dept)
                    intent.putExtra("usertype",user_type)
                    intent.putExtra("aboutme",aboutme)
                    intent.putExtra("github",github)
                    intent.putExtra("linkedin",linkedin)
                    startActivity(intent)
                }

            }

        }

        new_prj_domain.setOnClickListener {
            /**
             * adding a new tag to Tags
             */
            checkedIndex.add(new_domain.text.toString())
            input.append("\n"+new_domain.text)

            val url_append_tag = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"
            val append_tag = JSONObject().apply {
                put("dataSource", "Cluster0")
                put("database", "Intermatch")
                put("collection", "Tags")
                put("document", JSONObject().apply {
                    put("tag", new_domain.text)
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

        skip_btn.setOnClickListener {
            val intent =
                Intent(this@UploadProjectActivity, UploadInterestActivity::class.java)
            intent.putExtra("username", faculty_name)
            startActivity(intent)
        }

    }
}

