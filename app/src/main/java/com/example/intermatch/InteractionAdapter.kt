package com.example.intermatch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_inter_item.view.*

class InteractionAdapter  :BaseAdapter {

    lateinit var mcontext: Context
    lateinit var mylist :ArrayList<String>
    lateinit var faclist : ArrayList<String>
    lateinit var statlist : ArrayList<String>

    lateinit var inflater: LayoutInflater
    constructor(ct: Context, mylist : ArrayList<String>, faclist : ArrayList<String>,
                statlist : ArrayList<String>) {
        this.mcontext = ct
        this.mylist = mylist
        this.faclist = faclist
        this.statlist = statlist

        inflater = LayoutInflater.from(ct)
    }
    override fun getCount(): Int {
        return mylist.size
    }

    override fun getItem(position: Int): Any {
        return mylist[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView = inflater.inflate(R.layout.activity_inter_item,null)

        convertView.status_prj_name.text = mylist[position]
        convertView.status_fac_name.text = faclist[position]
        convertView.req_status.text = statlist[position]



        return convertView
    }
}