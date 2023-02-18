package com.example.intermatch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ProjectAdapter : BaseAdapter{
    lateinit var pcontext : Context
    lateinit var prj_List: ArrayList<String>
    lateinit var inflater : LayoutInflater
    lateinit var user_name: String
    constructor(
        ct: Context, prj_List: ArrayList<String>, user_name: String,
        ) {
        this.pcontext = ct
        this.prj_List = prj_List
        this.user_name = user_name

        inflater = LayoutInflater.from(ct)
    }

    override fun getCount(): Int {
        return prj_List.size
    }

    override fun getItem(position: Int): Any {
        return prj_List[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = inflater.inflate(R.layout.activity_custom_list_view,null)
        var txt : TextView = convertView.findViewById(R.id.list_text)
        txt.text = prj_List[position]
        var prj : ConstraintLayout = convertView.findViewById(R.id.list_item_layout)
        var delete : ImageView = convertView.findViewById(R.id.delete_btn)

        prj.setOnClickListener {
            val intent2 = Intent(pcontext,AboutProjectActivity::class.java)
            intent2.putExtra("prjname", req_projects.get(position))
            intent2.putExtra("faculty_name",user_name)
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(pcontext, intent2, null)
        }

        delete.setOnClickListener {

            val url_del = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/deleteOne"
            val volleyQueue = Volley.newRequestQueue(pcontext)
            val jsonfile_del = JSONObject().apply {
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","Project")
                put("filter", JSONObject().apply {
                    put("name",txt.text)
                    put("faculty_name",user_name)
                })
            }

            val request_del: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url_del, jsonfile_del,
                Response.Listener<JSONObject> { response ->
                    Toast.makeText(pcontext, "Project removed from list", Toast.LENGTH_LONG).show();

                },
                Response.ErrorListener { error ->
                    Toast.makeText(pcontext, error.message.toString(), Toast.LENGTH_LONG).show();

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
            volleyQueue.add(request_del)


            val intent1 = Intent(pcontext,StudentRequestActivity::class.java)
            intent1.putExtra("username",user_name)
            intent1.putExtra("position_name",txt.text)
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(pcontext, intent1, null)
        }
        return convertView

    }
}