package com.maveri.figma.main.widget

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.maveri.figma.R
import com.maveri.figma.repository.FirebaseRepository
import com.squareup.picasso.Picasso

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

        if(view == null) {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.photo_location_item, parent, false)
        }

        val card = view!!.findViewById<ImageView>(R.id.photo_card)

            Picasso.with(context)
                .load(images?.get(position))
                .into(card)

        return view
    }

}