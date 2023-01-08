package com.example.intermatch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startForegroundService
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_recommendation.*
import kotlinx.android.synthetic.main.activity_req_list_item.view.*
import kotlinx.android.synthetic.main.activity_show_profile.*
import org.json.JSONObject
import org.w3c.dom.Text

class RequestAdapter : BaseAdapter {
    lateinit var mcontext: Context
    lateinit var listrequested :ArrayList<String>
    lateinit var match_users : ArrayList<Int>
    lateinit var gits : ArrayList<String>
    lateinit var linkedin_list: ArrayList<String>
    lateinit var prj_name : String
    lateinit var fac_name : String
    lateinit var inflater: LayoutInflater
    constructor(ct: Context, requested_List : ArrayList<String>,match_users : ArrayList<Int>,
                gits : ArrayList<String>,linkedin_list : ArrayList<String>,prj_name : String,fac_name : String) {
        this.mcontext = ct
        this.listrequested = requested_List
        this.match_users = match_users
        this.gits = gits
        this.linkedin_list = linkedin_list
        this.prj_name = prj_name
        this.fac_name = fac_name
        inflater = LayoutInflater.from(ct)
    }
    override fun getCount(): Int {
        return listrequested.size
    }

    override fun getItem(position: Int): Any {
        return listrequested[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView = inflater.inflate(R.layout.activity_req_list_item,null)
        val req_username : TextView = convertView.findViewById(R.id.req_username)
        req_username.text = listrequested[position]
        val req_box : LinearLayout = convertView.findViewById(R.id.req_item_layout)

        val horizontal_layout : LinearLayout = convertView.findViewById(R.id.horizontal_layout)
        val accept : Button = horizontal_layout.findViewById(R.id.accept_btn)
        val reject : Button = horizontal_layout.findViewById(R.id.reject_btn)
        val prog_bar : ProgressBar = convertView.findViewById(R.id.match)
        prog_bar.progress = this.match_users[position]
        convertView.findViewById<TextView>(R.id.percent_text).text = this.match_users[position].toString() + "%"



        convertView.findViewById<TextView>(R.id.user_git).text = this.gits[position]
        convertView.findViewById<TextView>(R.id.user_linkedin).text = this.linkedin_list[position]

        val stud_github = convertView.findViewById<TextView>(R.id.user_git)
        val stud_linkedin = convertView.findViewById<TextView>(R.id.user_linkedin)
        val volleyQueue = Volley.newRequestQueue(mcontext)
        stud_github.setOnClickListener {
            var intent1 = Intent(Intent.ACTION_VIEW,Uri.parse(stud_github.text.toString()))
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mcontext,intent1,null)


        }

        stud_linkedin.setOnClickListener {
            var intent1 = Intent(Intent.ACTION_VIEW,Uri.parse(stud_linkedin.text.toString()))
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mcontext,intent1,null)
        }
        val url_update = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/updateOne"
        lateinit var jsonfile_update : JSONObject

        accept.setOnClickListener {
                jsonfile_update = JSONObject().apply {
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","Interested")
                put("filter", JSONObject().apply {
                    put("username",listrequested[position])
                    put("faculty_name",fac_name)
                    put("project_name",prj_name)

                })
                put("update",JSONObject().apply {
                    put("$"+"set",JSONObject().apply {
                        put("status","Accepted")
                    })
                })
            }

            val request_update : JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url_update, jsonfile_update,
                Response.Listener<JSONObject> { response ->


                },
                Response.ErrorListener { error ->

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
            request_update.setRetryPolicy(
                DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )

            volleyQueue.add(request_update)

        }
        reject.setOnClickListener {
            jsonfile_update = JSONObject().apply {
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","Interested")
                put("filter", JSONObject().apply {
                    put("username",listrequested[position])
                    put("faculty_name",fac_name)
                    put("project_name",prj_name)

                })
                put("update",JSONObject().apply {
                    put("$"+"set",JSONObject().apply {
                        put("status","Rejected")
                    })
                })
            }

            val request_update : JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url_update, jsonfile_update,
                Response.Listener<JSONObject> { response ->


                },
                Response.ErrorListener { error ->

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
            request_update.setRetryPolicy(
                DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )

            volleyQueue.add(request_update)

        }
        return convertView
    }
}