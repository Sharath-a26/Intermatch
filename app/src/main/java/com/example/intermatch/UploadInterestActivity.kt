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
import kotlinx.android.synthetic.main.activity_upload_interest.*
import kotlinx.android.synthetic.main.activity_upload_project.*
import org.json.JSONArray
import org.json.JSONObject

class UploadInterestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_interest)
        supportActionBar?.hide()



        val alertbuilder = AlertDialog.Builder(this)
        val user_name = intent.getStringExtra("username")
        var checkedIndex = ArrayList<String>()
        val arr =resources.getStringArray(R.array.data_list)
        var isclicked = BooleanArray(arr.size)
        for (i in 0 until arr.size) {
            isclicked[i] = false
        }
        select_inter_btn.setOnClickListener {
            Log.d(null,isclicked.toString())
            alertbuilder.setMultiChoiceItems(R.array.data_list,isclicked, DialogInterface.OnMultiChoiceClickListener{
                    dialog,index,checked ->

                if (input_interests.isVisible == true) {
                    input_interests.text.clear()
                }
                if (checked) {
                    //input.text?.append("\n ${arr.get(index)}")
                    isclicked[index] = checked
                    checkedIndex.add(arr.get(index))

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
                    val volleyQueue = Volley.newRequestQueue(this)
                    val url = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/insertOne"

                    val a = JSONObject().apply {
                        put("dataSource","Cluster0")
                        put("database","Intermatch")
                        put("collection","User")
                        put("document", JSONObject().apply {
                            put("username",user_name)
                            put("areas_of_interest",JSONArray().apply {
                                for (i in 0 until checkedIndex.size) {
                                    put(i,checkedIndex[i])
                                }
                            })
                        })
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


    }
}