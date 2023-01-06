package com.example.intermatch

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.Nullable
import kotlinx.android.synthetic.main.algo_spinner.view.*


class spinnerAdapter(
    context: Context?,
    spinnerList: ArrayList<spinner_item>,

    ) :
    ArrayAdapter<spinner_item?>(context!!, 0, spinnerList as List<spinner_item?>) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var inflater : LayoutInflater = LayoutInflater.from(context)
        var convertView = inflater.inflate(com.example.intermatch.R.layout.algo_spinner,null)
        val textViewName = convertView.text_view
        val currentItem: spinner_item? = getItem(position)

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            textViewName.setText(currentItem.getSpinnerItemName())
        }
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return getView(position, convertView, parent)
    }


}