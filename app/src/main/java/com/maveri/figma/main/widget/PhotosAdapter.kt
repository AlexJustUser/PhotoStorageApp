package com.maveri.figma.main.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.maveri.figma.databinding.PhotoLocationItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotosAdapter @JvmOverloads constructor(private val context: Context, private val images: MutableList<String>?) :  BaseAdapter() {

    private val binding = PhotoLocationItemBinding.inflate(LayoutInflater.from(context), this, true)

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
            view = layoutInflater.inflate(, parent, false)
        }

//        val card = view!!.findViewById<ImageView>(R.id.image_card)
//        val name : TextView = view.findViewById(R.id.text_card_name)
//        val description : TextView = view.findViewById(R.id.text_card_short_description)

//        Glide.with(context)
//            .load(images[position]["image_card"])
//            .into(card)

//        name.text = images[position]["text_card_name"]
//        description.text = images[position]["text_card_short_description"]

        return view
    }

}