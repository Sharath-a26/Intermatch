package com.example.intermatch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_custom_list_view.view.*
import org.json.JSONObject

/*
    creating a base adapter to link the contents into the liked list page.
    Using list view
 */
class CustomBaseAdapter : BaseAdapter {
    lateinit var bcontext: Context
    lateinit var listliked :ArrayList<String>
    lateinit var inflater: LayoutInflater
    lateinit var user_name: String
    lateinit var user_inter : ArrayList<CharSequence>
    lateinit var user_github: String
    lateinit var user_linkedin: String
    constructor(
        ct:Context, liked_List: ArrayList<String>, user_name: String,
        user_inter: java.util.ArrayList<CharSequence>?, user_github: String, user_linkedin: String) {
        this.bcontext = ct
        this.listliked = liked_List
        this.user_name = user_name

        if (user_inter != null) {
            this.user_inter = user_inter
        }

        this.user_github = user_github
        this.user_linkedin = user_linkedin
        inflater = LayoutInflater.from(ct)
    }
    override fun getCount(): Int {
        return listliked.size
    }

    override fun getItem(position: Int): Any {
        return listliked[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = inflater.inflate(R.layout.activity_custom_list_view,null)
        var txt : TextView = convertView.findViewById(R.id.list_text)
        txt.text = listliked[position]
        var prj : ConstraintLayout = convertView.findViewById(R.id.list_item_layout)
        var delete : ImageView = convertView.findViewById(R.id.delete_btn)

        prj.setOnClickListener {
            val intent1 = Intent(bcontext,SearchActivity::class.java)
            intent1.putExtra("keyword",convertView.list_text.text)
            intent1.putExtra("user_interest",this.user_inter)
            intent1.putExtra("github",this.user_github)
            intent1.putExtra("linkedin",this.user_linkedin)
            intent1.putExtra("username",user_name)
            intent1.putStringArrayListExtra("listliked",listliked)
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(bcontext,intent1,null)

        }
        delete.setOnClickListener {

            val url_del = "https://data.mongodb-api.com/app/data-hpjly/endpoint/data/v1/action/deleteOne"
            val volleyQueue = Volley.newRequestQueue(bcontext)
            val jsonfile_del = JSONObject().apply {
                put("dataSource","Cluster0")
                put("database","Intermatch")
                put("collection","Liked_projects")
                put("filter", JSONObject().apply {
                    put("name",txt.text)
                    put("username",user_name)
                })
            }

            val request_del: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url_del, jsonfile_del,
                Response.Listener<JSONObject> { response ->
                    Toast.makeText(bcontext, "Project removed from liked list", Toast.LENGTH_LONG).show();

                },
                Response.ErrorListener { error ->
                    Toast.makeText(bcontext, error.message.toString(), Toast.LENGTH_LONG).show();

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
            val intent1 = Intent(bcontext,LikedActivity::class.java)
            intent1.putExtra("username",user_name)
            intent1.putExtra("position_name",txt.text)
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(bcontext,intent1,null)

        }

        return convertView
    }
}

