package com.example.intermatch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class TagAdapter : BaseAdapter {

    lateinit var tcontext : Context
    lateinit var prj_List: ArrayList<String>
    lateinit var inflater : LayoutInflater
    lateinit var user_name: String
    constructor(
        ct: Context, prj_List: ArrayList<String>, user_name: String,
    ) {
        this.tcontext = ct
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
        delete.isVisible = false
        prj.setOnClickListener {
            val intent2 = Intent(tcontext,SearchActivity::class.java)
            intent2.putExtra("keyword",txt.text)
            var interests = ArrayList<String>()
            for (x in 0 until user_interest.length()) {
                interests.add(user_interest.getString(x))
            }
            intent2.putExtra("user_interest", interests)
            intent2.putExtra("github", user_github)
            intent2.putExtra("linkedin", user_linkedin)
            intent2.putExtra("username",user_name)
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(tcontext, intent2, null)

        }


        return convertView
    }
}