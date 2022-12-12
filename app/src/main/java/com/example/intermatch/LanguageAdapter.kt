package com.example.intermatch

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
/*
    using a language adapter to render contents for the grid view
    Grid View is used to display the domains of the particular project
 */
class LanguageAdapter(var context : Context, var arrayList : ArrayList<LanguageItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return arrayList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view : View = View.inflate(context,R.layout.grid_list_item,null)

        var languageitem: LanguageItem = arrayList.get(position)
        var names : TextView = view.findViewById(R.id.names)
        names.text = languageitem.item
        return view
    }
}