package com.maveri.figma.main.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide

class PhotosAdapter constructor(private val context: Context, private val images: MutableList<String>?) :  BaseAdapter() {



    override fun getCount(): Int {
        return images!!.size
    }

    override fun getItem(position: Int): String? {
        return images?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView

        //view = images?.get(position)?.let { it -> PhotoItemView(context).setItem(it, position, this) }



        return view
    }

}