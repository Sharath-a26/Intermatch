package com.example.intermatch

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity

/*
    creating a base adapter to link the contents into the liked list page.
    Using list view
 */
class CustomBaseAdapter : BaseAdapter {
    lateinit var context: Context
    lateinit var listliked :ArrayList<String>
    lateinit var inflater: LayoutInflater
    constructor(ct:Context,liked_List : ArrayList<String>) {
        this.context = ct
        this.listliked = liked_List
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
        var txt : TextView = convertView.findViewById(R.id.text)
        txt.text = listliked[position]
        var prj : ConstraintLayout = convertView.findViewById(R.id.list_item_layout)
        var delete : ImageView = convertView.findViewById(R.id.delete_btn)

        prj.setOnClickListener {
            startActivity(context.applicationContext,Intent(context.applicationContext,AboutProjectActivity::class.java),null)
        }
        delete.setOnClickListener {
            Log.d(null,"delete pressed")
        }
        return convertView
    }
}