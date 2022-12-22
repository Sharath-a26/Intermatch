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
import kotlinx.android.synthetic.main.activity_upload_project.*
import org.json.JSONArray
import org.json.JSONObject

class UploadProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_project)
        val faculty_email = intent.getStringExtra("faculty_email") //getting fac_email from register page
        val faculty_name = intent.getStringExtra("faculty_name") // getting fac_name from register page
        val alertbuilder = AlertDialog.Builder(this)
        var checkedIndex = ArrayList<String>()
        val arr =resources.getStringArray(R.array.data_list)
        var isclicked = BooleanArray(arr.size)
        for (i in 0 until arr.size) {
            isclicked[i] = false
        }
        upload_domain_btn.setOnClickListener {

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

            addprj.text = "NEXT"
            val volleyQueue = Volley.newRequestQueue(this)
            val description = edit_desc.text
            val url =
                "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/updateOne"
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
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","Project")
                put("filter",JSONObject().apply {
                    put("username",faculty_name)

                })
                put("update",JSONObject().apply {
                    put("$"+"set",JSONObject().apply {
                        put("faculty_name",faculty_name)
                        put("name",project_name)
                        put("faculty_email",faculty_email)
                        put("domains", JSONArray().apply {
                            for (i in 0..checkedIndex.size-1) {
                                put(i,checkedIndex[i])
                            }
                        }

                        )
                        put("desc",description)
                    })
                })

            }
            Log.d(null,"helo")
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
                    headers.put("Access-Control-Request-Headers","*");

                    return headers
                }
            }
            volleyQueue.add(request)

            addprj.setOnClickListener {
                val intent = Intent(this@UploadProjectActivity,UploadInterestActivity::class.java)
                intent.putExtra("username",faculty_name)
                startActivity(intent)
            }

        }


    }
}